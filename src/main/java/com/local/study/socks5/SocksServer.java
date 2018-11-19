package com.local.study.socks5;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 标准的socks代理服务器，支持sock5与sock4代理
 *
 * @author Administrator
 *
 */
public class SocksServer implements Runnable {

	/**
	 * 来源的代理socket
	 */
	private final Socket socket;
	/**
	 * 是否开启socks4代理
	 */
	private final boolean openSock4;
	/**
	 * 是否开启socks5代理
	 */
	private final boolean openSock5;
	/**
	 * socks5代理的登录用户名，如果不为空表示需要登录验证
	 */
	private final String user;
	/**
	 * socks5代理的登录密码，
	 */
	private final String pwd;
	/**
	 * socks是否需要进行登录验证
	 */
	private final boolean socksNeedLogin;

	/**
	 * @param socket
	 *            来源的代理socket
	 * @param openSock4
	 *            是否开启socks4代理
	 * @param openSock5
	 *            是否开启socks5代理
	 * @param user
	 *            socks5代理的登录用户名，如果 不为空表示需要登录验证
	 * @param pwd
	 *            socks5代理的登录密码，
	 */
	private SocksServer(Socket socket, boolean openSock4, boolean openSock5, String user, String pwd) {
		this.socket = socket;
		this.openSock4 = openSock4;
		this.openSock5 = openSock5;
		this.user = user;
		this.pwd = pwd;
		this.socksNeedLogin = null != user;
	}

	public void run() {
		// 获取来源的地址用于日志打印使用
		String address = socket.getRemoteSocketAddress().toString();
		log("process one socket : %s", address);
		// 声明流
		InputStream a_in = null, b_in = null;
		OutputStream a_out = null, b_out = null;
		Socket proxy_socket = null;
		try {
			a_in = socket.getInputStream();
			a_out = socket.getOutputStream();

			// 获取协议头。取代理的类型，只有 4，5。
			byte[] tmp = new byte[1];
			int n = a_in.read(tmp);
			if (n == 1) {
				byte protocol = tmp[0];
				if ((openSock4 && 0x04 == protocol)) {// 如果开启代理4，并以socks4协议请求
					proxy_socket = sock4Check(a_in, a_out);
				} else if ((openSock5 && 0x05 == protocol)) {// 如果开启代理5，并以socks5协议请求
					proxy_socket = sock5Check(a_in, a_out);
				} else {// 非socks 4 ,5 协议的请求
					log("not socks proxy : %s  openSock4[] openSock5[]", tmp[0], openSock4, openSock5);
				}
				if (null != proxy_socket) {
					CountDownLatch latch = new CountDownLatch(1);
					b_in = proxy_socket.getInputStream();
					b_out = proxy_socket.getOutputStream();
					// 交换流数据
					transfer(latch, a_in, b_out);
					transfer(latch, b_in, a_out);
					try {
						latch.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				log("socks error : %s", Arrays.toString(tmp));
			}
		} catch (Exception e) {
			log("exception : %s %s", e.getClass(), e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			log("close socket, system cleanning ...  %s ", address);
			closeIo(a_in);
			closeIo(b_in);
			closeIo(b_out);
			closeIo(a_out);
			closeIo(socket);
			closeIo(proxy_socket);
		}
	}

	/**
	 * sock5代理头处理
	 *
	 */
	private Socket sock5Check(InputStream in, OutputStream out) throws IOException {
		byte[] tmp = new byte[2];
		in.read(tmp);
		boolean isLogin = false;
		byte method = tmp[1];
		if (0x02 == tmp[0]) {
			method = 0x00;
			in.read();
		}
		if (socksNeedLogin) {
			method = 0x02;
		}
		tmp = new byte[]{0x05, method};
		out.write(tmp);
		out.flush();
		// Socket result = null;
		Object resultTmp = null;
		if (0x02 == method) {// 处理登录.
			int b = in.read();
			String user ;
			String pwd ;
			if (0x01 == b) {
				b = in.read();
				tmp = new byte[b];
				in.read(tmp);
				user = new String(tmp);
				b = in.read();
				tmp = new byte[b];
				in.read(tmp);
				pwd = new String(tmp);
				if (user.trim().equals(this.user) && pwd.trim().equals(this.pwd)) {// 权限过滤
					isLogin = true;
					tmp = new byte[]{0x05, 0x00};// 登录成功
					out.write(tmp);
					out.flush();
					log("%s login success !", user);
				} else {
					log("%s login faild !", user);
				}
			}
		}
		byte cmd = 0;
		if (!socksNeedLogin || isLogin) {// 验证是否需要登录
			tmp = new byte[4];
			in.read(tmp);
			log("proxy header >>  %s", Arrays.toString(tmp));
			cmd = tmp[1];
			String host = getHost(tmp[3], in);
			tmp = new byte[2];
			in.read(tmp);
			int port = ByteBuffer.wrap(tmp).asShortBuffer().get() & 0xFFFF;
			log("connect %s:%s", host, port);
			ByteBuffer rsv = ByteBuffer.allocate(10);
			rsv.put((byte) 0x05);
			try {
				if (0x01 == cmd) {//connect 建立tcp连接
					resultTmp = new Socket(host, port);
					rsv.put((byte) 0x00);
				} else if (0x02 == cmd) {//bind 上报反向连接地址？
					resultTmp = new ServerSocket(port);
					rsv.put((byte) 0x00);
				} else {
					rsv.put((byte) 0x05);
					resultTmp = null;
				}
			} catch (Exception e) {
				rsv.put((byte) 0x05);
				resultTmp = null;
			}
			rsv.put((byte) 0x00);
			rsv.put((byte) 0x01);
			rsv.put(socket.getLocalAddress().getAddress());
			Short localPort = (short) ((socket.getLocalPort()) & 0xFFFF);
			rsv.putShort(localPort);
			tmp = rsv.array();
		} else {
			tmp = new byte[]{0x05, 0x01};// 登录失败
			log("socks server need login,but no login info .");
		}
		out.write(tmp);
		out.flush();
		if (null != resultTmp && 0x02 == cmd) {
			ServerSocket ss = (ServerSocket) resultTmp;
			try {
				resultTmp = ss.accept();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeIo(ss);
			}
		}
		return (Socket) resultTmp;
	}

	/**
	 * sock4代理的头处理
	 *
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private Socket sock4Check(InputStream in, OutputStream out) throws IOException {
		Socket proxy_socket = null;
		byte[] tmp = new byte[3];
		in.read(tmp);
		// 请求协议|VN1|CD1|DSTPORT2|DSTIP4|NULL1|
		int port = ByteBuffer.wrap(tmp, 1, 2).asShortBuffer().get() & 0xFFFF;
		String host = getHost((byte) 0x01, in);
		in.read();
		byte[] rsv = new byte[8];// 返回一个8位的响应协议
		// |VN1|CD1|DSTPORT2|DSTIP 4|
		try {
			proxy_socket = new Socket(host, port);
			log("connect [%s] %s:%s", tmp[1], host, port);
			rsv[1] = 90;// 代理成功
		} catch (Exception e) {
			log("connect exception  %s:%s", host, port);
			rsv[1] = 91;// 代理失败.
		}
		out.write(rsv);
		out.flush();
		return proxy_socket;
	}

	/**
	 * 获取目标的服务器地址
	 *
	 */
	private String getHost(byte type, InputStream in) throws IOException {
		String host = null;
		byte[] tmp;
		switch (type) {
			case 0x01:// IPV4协议
				tmp = new byte[4];
				in.read(tmp);
				host = InetAddress.getByAddress(tmp).getHostAddress();
				break;
			case 0x03:// 使用域名
				int n = in.read();
				tmp = new byte[n];
				in.read(tmp);
				host = new String(tmp);
				break;
			case 0x04:// 使用IPV6
				tmp = new byte[16];
				in.read(tmp);
				host = InetAddress.getByAddress(tmp).getHostAddress();
				break;
			default:
				break;
		}
		return host;
	}

	/**
	 * IO操作中共同的关闭方法
	 *
	 */
	private static void closeIo(Socket closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * IO操作中共同的关闭方法
	 *
	 */
	private static void closeIo(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 数据交换.主要用于tcp协议的交换
	 *
	 * @createTime 2014年12月13日 下午11:06:47
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 */
	private static void transfer(final CountDownLatch latch, final InputStream in, final OutputStream out) {
		new Thread() {
			public void run() {
				byte[] bytes = new byte[1024];
				int n;
				try {
					while ((n = in.read(bytes)) > 0) {
						out.write(bytes, 0, n);
						out.flush();
					}
				} catch (Exception e) {
				}
				if (null != latch) {
					latch.countDown();
				}
			}

			;
		}.start();
	}

	private static void log(String message, Object... args) {
		Date dat = new Date();
		String msg = String
				.format("%1$tF %1$tT %2$-5s %3$s%n", dat, Thread.currentThread().getId(), String.format(message, args));
		System.out.print(msg);
	}

	private static void startServer(int port, boolean openSock4, boolean openSock5, String user, String pwd)
			throws IOException {
		log("config >> port[%s] openSock4[%s] openSock5[%s] user[%s] pwd[%s]", port, openSock4, openSock5, user, pwd);
		ServerSocket ss = new ServerSocket(port);
		Socket socket = null;
		log("Socks server port : %s listenning...", port);
		while (null != (socket = ss.accept())) {
			new Thread(new SocksServer(socket, openSock4, openSock5, user, pwd)).start();
		}
		ss.close();
	}

	public static void main(String[] args) throws IOException {
//		java.security.Security.setProperty("networkaddress.cache.ttl", "86400");
		int port = 1080;
		String user = "user";
		String pwd = "123456";
		SocksServer.startServer(port, true, true, user, pwd);
	}
}
