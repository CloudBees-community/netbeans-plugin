package com.cloudbees.netbeans.modules.service.plugin.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * handling of stdin, stdout and stderr from the cloudbees application started  
 * from the services tab. (derived from the mavenide code) 
 * 
 * @author David BRASSELY 
 */
public class IOHandler {

    private static final String IN_LINE_PREFIX = "&^#INCOMPLINE:";
    private static final long TASK_FINISH_TIMEOUT = 5000;
    private static final RequestProcessor PROCESSOR = new RequestProcessor("Cloudbees Application IO Handlers", 100, true); //NOI18N
    private static final Logger LOG = Logger.getLogger(IOHandler.class.getName());
    private InputOutput mIO;
    private OutputWriter stdOut;
    private OutputWriter stdErr;
    private Task mOutTask;
    private Task mErrTask;
    private Task mInTask;
    private InHandler mInHandler;
    private IOHandler() {
    }

    protected IOHandler(InputOutput io) {
        this();
        // System.err.println("Creating IO Handler....");
        this.mIO = io;
        stdOut = this.mIO.getOut();
        stdErr = this.mIO.getErr();
    }

    public IOHandler(InputOutput io, InputStream is, OutputStream os) {
        this(io);
        startHandlers(is, os);
    }
    
    public void writeInput(String text) {
        if ( mInHandler != null ) {
            mInHandler.writeInput(text);
        }
    }
    
    public void startHandlers(InputStream is, OutputStream os) {
        //Get the standard out
        InputStream out = new BufferedInputStream(is, 8192);
        //Get the standard in
        //TODO: set the stdin processing optional
        OutputStream in = os;

        mInHandler = new InHandler(in, mIO);
        mInTask = PROCESSOR.post(mInHandler, 5000);

        mOutTask = PROCESSOR.post(new OutHandler(out, stdOut));
    }

    private void stopTask(Task task) {
        try {
            boolean finished = false;
            finished = task.waitFinished(TASK_FINISH_TIMEOUT);
            if (!finished) {
                task.cancel();
            }
        } catch (InterruptedException ex) {
            task.cancel();
        }
    }
    
    public void stopHandlers() {
        if (mInHandler != null) {
            mInHandler.stopInput();
        }
        stopTask(mInTask);
        stopTask(mOutTask);
        stopTask(mErrTask);
    }

    protected final void processMultiLine(String input, OutputWriter writer) {
        if (input == null) {
            return;
        }
        String[] strs = splitMultiLine(input);
        for (int i = 0; i < strs.length; i++) {
            processLine(strs[i], writer);
        }
    }

    protected final void processLine(String input, OutputWriter writer) {
        writer.println(input);
    }

    public static String[] splitMultiLine(String input) {
        List<String> list = new ArrayList<String>();
        String[] strs = input.split("\\r|\\n"); //NOI18N
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].length() > 0) {
                list.add(strs[i]);
            }
        }
        return list.toArray(new String[0]);
    }

    private class OutHandler implements Runnable {

        private BufferedReader mInReader;
        private boolean mSkiptLF = false;
        OutputWriter mOutWriter;

        public OutHandler(InputStream inStream, OutputWriter outWriter) {
            mInReader = new BufferedReader(new InputStreamReader(inStream));
            this.mOutWriter = outWriter;
        }

        private String readLine() throws IOException {
            char[] char1 = new char[1];
            boolean isReady = true;
            StringBuffer buf = new StringBuffer();
            while (isReady) {
                int ret = mInReader.read(char1);
                if (ret != 1) {
                    if (ret == -1 && buf.length() == 0) {
                        return null;
                    }
                    return buf.toString();
                }
                if (mSkiptLF) {
                    mSkiptLF = false;
                    if (char1[0] == '\n') {
                        continue;
                    }
                }
                if (char1[0] == '\n') {
                    return buf.toString();
                }
                if (char1[0] == '\r') {
                    mSkiptLF = true;
                    return buf.toString();
                }
                buf.append(char1[0]);
                isReady = mInReader.ready();
                if (!isReady) {
                    synchronized (this) {
                        try {
                            wait(500);
                        } catch (InterruptedException ex) {
                            LOG.log(Level.FINE, ex.getMessage(), ex);
                        } finally {
                            if (!mInReader.ready()) {
                                break;
                            }
                            isReady = true;
                        }
                    }

                }
            }
            return IN_LINE_PREFIX + buf.toString();

        }

        public void run() {
            try {
                String line = readLine();
                while (line != null) {
                    if (line.startsWith(IN_LINE_PREFIX)) {
                        mOutWriter.print(line.substring(IN_LINE_PREFIX.length()));
                        line = readLine();
                        continue;
                    }
                    processLine(line, mOutWriter); //NOI18N
                    line = readLine();
                }
            } catch (IOException ex) {
                LOG.log(Level.FINE, ex.getMessage(), ex);
            } finally {
                try {
                    mInReader.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private static class InHandler implements Runnable {

        private InputOutput mIO;
        private OutputStream mOutStr;
        private boolean stopIn = false;

        public InHandler(OutputStream out, InputOutput inputOutput) {
            mOutStr = out;
            mIO = inputOutput;
        }

        public void stopInput() {
            stopIn = true;
        }

        public void writeInput(String text) {
          try {
            if ( mOutStr != null ) {
                mOutStr.write(text.getBytes());
                mOutStr.flush();
            }
          } catch (Exception ex) {
              // ignore.
          }
        }

        public void run() {
            Reader in = mIO.getIn();
            try {
                try {
                    String empty = "\n\n";
                    mOutStr.write(empty.getBytes());
                    mOutStr.flush();
                } catch (Exception ex) {
                    // ignore
                }
                while (true) {
                    int read = in.read();
                    if (read != -1) {
                        mOutStr.write(read);
                        mOutStr.flush();
                    } else {
                        mOutStr.close();
                        LOG.info("#### IOHandler.InHandler end: read -1");
                        return;
                    }
                    if (stopIn) {
                        LOG.info("#### IOHandler.InHandler end: stopped");
                        return;
                    }
                }

            } catch (IOException ex) {
                LOG.log(Level.FINE, ex.getMessage(), ex);
            } finally {
                try {
                    mOutStr.close();
                } catch (IOException ex) {
                }
            }
            LOG.info("#### IOHandler.InHandler end");
        }
    }
}
