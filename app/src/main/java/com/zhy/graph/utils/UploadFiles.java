package com.zhy.graph.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class UploadFiles {
	// 定义一个StringBuilder参数
	private static StringBuilder result = null;

	public static JSONObject sendPOSTRequest(final String path,
			final Map<String, String> params, final FormFile[] files)
			throws Exception {
		FutureTask<JSONObject> task = new FutureTask<JSONObject>(
				new Callable<JSONObject>() {

					@Override
					public JSONObject call() throws Exception {
						// 声明一个字符串常量BOUNDARY来作为数据分隔线
						final String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
						// 数据结束标志字符串常量
						final String endline = "--" + BOUNDARY + "--\r\n";
						// 通过URL来建立HttpURLConnection链接
						HttpURLConnection connection = (HttpURLConnection) new URL(
								path).openConnection();
						// 允许对输出数据
						connection.setDoOutput(true);
						// 允许对内输出数据
						connection.setDoInput(true);
						// 禁止使用缓存
						connection.setUseCaches(false);
						connection.setConnectTimeout(5000);
						// 设置Request请求方式
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Connection",
								"Keep-Alive");
						// 设置字符格式为Utf-8
						connection.setRequestProperty("Charset", "UTF-8");
						// 设置content-type
						connection.setRequestProperty("Content-Type",
								"multipart/form-data;boundary=" + BOUNDARY);
						int fileDataLength = 0;
						// 得到文件类型数据的总长度，
						// 这里暂时没有用到
						for (FormFile uploadFile : files) {
							StringBuilder fileExplain = new StringBuilder();
							fileExplain.append("--");
							fileExplain.append(BOUNDARY);
							fileExplain.append("\r\n");
							fileExplain
									.append("Content-Disposition: form-data;name=\""
											+ uploadFile.getParameterName()
											+ "\";filename=\""
											+ uploadFile.getFilname()
											+ "\"\r\n");
							fileExplain.append("Content-Type: "
									+ uploadFile.getContentType() + "\r\n\r\n");
							fileExplain.append("\r\n");
							fileDataLength += fileExplain.length();
							if (uploadFile.getInStream() != null) {
								fileDataLength += uploadFile.getFile().length();
							} else {
								fileDataLength += uploadFile.getData().length;
							}
						}
						StringBuilder textEntity = new StringBuilder();
						// 构造文本类型参数的实体数据
						for (Map.Entry<String, String> entry : params
								.entrySet()) {
							textEntity.append("--");
							textEntity.append(BOUNDARY);
							textEntity.append("\r\n");
							textEntity
									.append("Content-Disposition: form-data; name=\""
											+ entry.getKey() + "\"\r\n\r\n");
							textEntity.append(entry.getValue());
							textEntity.append("\r\n");
						}
						// 计算传输给服务器的实体数据总长度
						int dataLength = textEntity.toString().getBytes().length
								+ fileDataLength + endline.getBytes().length;

						/* 设置DataOutputStream */
						DataOutputStream outStream = new DataOutputStream(
								connection.getOutputStream());
						// 把所有文本类型的实体数据发送出来
						outStream.write(textEntity.toString().getBytes());
						// 上传的文件部分，格式请参考文章
						// 把所有文件类型的实体数据发送出来
						for (FormFile uploadFile : files) {
							StringBuilder fileEntity = new StringBuilder();
							String content_type = "image/jpeg";
							// 设置上传图片的content-type
							if (uploadFile.getFilname().endsWith(".jpg")) {
								content_type = "image/jpeg";
							} else if (uploadFile.getFilname().endsWith(".png")) {
								content_type = "image/png";
							} else if (uploadFile.getFilname().endsWith(".gif")) {
								content_type = "image/gif";
							} else if (uploadFile.getFilname().endsWith(".bmp")) {
								content_type = "image/bmp";
							}
							fileEntity.append("--");
							fileEntity.append(BOUNDARY);
							fileEntity.append("\r\n");
							fileEntity
									.append("Content-Disposition: form-data;name=\""
											+ uploadFile.getParameterName()
											+ "\";filename=\""
											+ uploadFile.getFilname()
											+ "\"\r\n");
							fileEntity.append("Content-Type: " + content_type
									+ "\r\n\r\n");
							outStream.write(fileEntity.toString().getBytes());
							if (uploadFile.getInStream() != null) {
								byte[] buffer = new byte[1024];
								int len = 0;
								while ((len = uploadFile.getInStream().read(
										buffer, 0, 1024)) != -1) {
									outStream.write(buffer, 0, len);
								}
								uploadFile.getInStream().close();
							} else {
								outStream.write(uploadFile.getData(), 0,
										uploadFile.getData().length);
							}
							outStream.write("\r\n".getBytes());
						}
						// 下面发送数据结束标志，表示数据已经结束
						outStream.write(endline.getBytes());
						// 数据一次性提交
						outStream.flush();

						int httpCode = connection.getResponseCode();

						if (httpCode == HttpURLConnection.HTTP_OK
								&& connection != null) {
							// 读取服务器返回的json数据（接受json服务器数据）
							InputStream inputStream = connection
									.getInputStream();
							InputStreamReader inputStreamReader = new InputStreamReader(
									inputStream);
							// 读字符串用的。
							BufferedReader reader = new BufferedReader(
									inputStreamReader);
							String s;
							result = new StringBuilder();
							while (((s = reader.readLine()) != null)) {
								result.append(s);
							}
							// 关闭输入流
							reader.close();
							// 在这里把result这个字符串个给JSONObject。解读里面的内容。
							JSONObject jsonObject = new JSONObject(result
									.toString());
							return jsonObject;
						}
						outStream.close();
						return null;
					}
				});
		new Thread(task).start();
		return task.get();

	}
}
