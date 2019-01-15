package android_serialport_api;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;



public class SerialPort {

	private Logger logger = Logger.getLogger(SerialPort.class);
	private static final String TAG = "SerialPort";
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	private ReentrantLock mylock = new ReentrantLock();

	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	public void release(){
		if(mFileInputStream!=null){
			try {
				mFileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(mFileOutputStream!=null){
			try {
				mFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.close();
	}
	private byte xorchk(byte[] b, int offset, int length) {
		byte chk = 0;
		int i;
		for (i = 0; i < length; i++) {
			chk ^= b[offset + i];
		}
		return chk;
	}

	public byte[] sendAndGet(byte[] cmd){
		byte[] data;
		try {
			mylock.lock();
			mFileOutputStream.write(cmd);
			Thread.sleep(50);
			ByteBuffer mRecvData = ByteBuffer.allocate(1024*4);
			long TickCount = System.currentTimeMillis();
			int recvlen = 0;

			while (mFileInputStream.available()>0){
				byte[] buf=new byte[1024];
				int size = mFileInputStream.read(buf);
				if (size > 0) {
					mRecvData.put(buf, recvlen, size);
					recvlen+=size;
				}
				Thread.sleep(10);//必须要休眠一下，，，，，，，确保数据都返回了！！！！！！
			}
			data=new byte[recvlen];
			if(recvlen>0){
				System.arraycopy(mRecvData.array(), 0, data, 0, recvlen);
			}
			return data;
		} catch (Exception ex) {
			logger.error("串口读写出错",ex);
			return null;
		}finally{
			mylock.unlock();
		}
	}

	public byte[] getOnly(){
		byte[] data;
		try {
			mylock.lock();
			Thread.sleep(50);
			ByteBuffer mRecvData = ByteBuffer.allocate(1024*4);
			long TickCount = System.currentTimeMillis();
			int recvlen = 0;

			while (mFileInputStream.available()>0){
				byte[] buf=new byte[1024];
				int size = mFileInputStream.read(buf);
				if (size > 0) {
					mRecvData.put(buf, recvlen, size);
					recvlen+=size;
				}
				Thread.sleep(10);//必须要休眠一下，，，，，，，确保数据都返回了！！！！！！
			}
			data=new byte[recvlen];
			if(recvlen>0){
				System.arraycopy(mRecvData.array(), 0, data, 0, recvlen);
			}
			return data;
		} catch (Exception ex) {
			logger.error("串口读写出错",ex);
			return null;
		}finally{
			mylock.unlock();
		}
	}

	/**
	 * 进行串口通信
	 * @param cmd
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	public void testSendCOM(byte[] cmd) throws IOException {
		try {
			mylock.lock();
			ByteBuffer mRecvData = ByteBuffer.allocate(1024*4);
			long TickCount = System.currentTimeMillis();
			int recvlen = 0;
			mFileOutputStream.write(cmd);
			System.out.println("写入完成");
		} catch (IOException ex) {
			throw ex;//抛出IO读取异常
		}finally{
			mylock.unlock();
		}
	}

		public byte[] testGetCOM() throws IOException {
		try {
			mylock.lock();
			ByteBuffer mRecvData = ByteBuffer.allocate(1024*4);
			long TickCount = System.currentTimeMillis();
			int recvlen = 0;
			while (mFileInputStream.available()>0){
				byte[] buf=new byte[1024];
				int size = mFileInputStream.read(buf);
				if (size > 0) {
					mRecvData.put(buf, recvlen, size);
					recvlen+=size;
				}
				Thread.sleep(10);//必须要休眠一下，，，，，，，确保数据都返回了！！！！！！
			}
			byte[] data=new byte[recvlen];
			if(recvlen>0){
				System.arraycopy(mRecvData.array(), 0, data, 0, recvlen);
			}
			return data;
		} catch (IOException ex) {
			throw ex;//抛出IO读取异常
		} catch (InterruptedException ex) {
			return null;
		}finally{
			mylock.unlock();
		}
	}

	 
	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port");
	}
}
