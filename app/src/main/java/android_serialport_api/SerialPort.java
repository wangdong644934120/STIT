package android_serialport_api;

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

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
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
//	public byte[] doBulkTransfer(byte[] cmd, long timeout) throws IOException {
//		try {
//			mylock.lock();
//			ByteBuffer mRecvData = ByteBuffer.allocate(1024*4);
//			long TickCount = System.currentTimeMillis();
//			int recvlen = 0;
//			mFileOutputStream.write(cmd);
//			while (mFileInputStream.available()<7 && (System.currentTimeMillis()-TickCount)<timeout){
//				Thread.sleep(10);
//			}
//			while (mFileInputStream.available()>0){
//				byte[] buf=new byte[1024];
//				int size = mFileInputStream.read(buf);
//				if (size > 0) {
//					mRecvData.put(buf, recvlen, size);
//					recvlen+=size;
//				}
//				Thread.sleep(10);//必须要休眠一下，，，，，，，确保数据都返回了！！！！！！
//			}
//			byte[] data=new byte[recvlen];
//			if(recvlen>0){
//				System.arraycopy(mRecvData.array(), 0, data, 0, recvlen);
//			}
//			return data;
//		} catch (IOException ex) {
//			throw ex;//抛出IO读取异常
//		} catch (InterruptedException ex) {
//			return null;
//		}finally{
//			mylock.unlock();
//		}
//	}
		
	/**
	 * 读取二代证的通信方法
	 * @param cmd
	 * @param timeout
	 * @return
	 */
	public byte[] readBaseMsg(byte[] cmd, long timeout) {
		byte[] recv = new byte[7], recvl = null;
		long TickCount;
		int recvlen = 0;
		try {
			mylock.lock();
			mFileOutputStream.write(cmd);
			TickCount = System.currentTimeMillis();
			while (mFileInputStream.available()<7){
				if((System.currentTimeMillis()-TickCount)>timeout){
					return null;
				}
				Thread.sleep(10);
			}
			if (mFileInputStream.read(recv) != recv.length 
					|| recv[0] != (byte)0xAA 
					|| recv[1] != (byte)0xAA
					|| recv[2] != (byte)0xAA
					|| recv[3] != (byte)0x96
					|| recv[4] != (byte)0x69) {
				while (mFileInputStream.available()>0){
					mFileInputStream.read();
				}
				return null;
			}
			recvlen = recv[5] * 256 + recv[6];
			//Log.d("串口调试", "接收长度:"+recvlen);
			while (mFileInputStream.available()<recvlen && (System.currentTimeMillis()-TickCount)<timeout){
				Thread.sleep(10);
			}
			if (mFileInputStream.available()<recvlen || recvlen < 4) {//超时
				while (mFileInputStream.available()>0)
					mFileInputStream.read();
				return null;
			}
			recvl = new byte[recv.length+recvlen];
			System.arraycopy(recv, 0, recvl, 0, recv.length);
			if (mFileInputStream.read(recvl, recv.length, recvlen) != recvlen) {
				while (mFileInputStream.available()>0)
					mFileInputStream.read();
				return null;
			}
			while (mFileInputStream.available()>0)
				mFileInputStream.read();
			if (xorchk(recvl, 5, recvl.length-5)!=0)
				return null;			
		} catch (Exception ex) {
			ex.printStackTrace();
			recvl = null;
		}finally{
			mylock.unlock();
		}
		return recvl;
	}
	 
	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
	static {
		System.loadLibrary("serial_port");
	}
}
