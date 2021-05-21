package com.xteq.asc;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jcraft.jsch.*;
import com.xteq.asc.misc.CustomeUserInfo;
//import jdk.internal.vm.compiler.collections.EconomicMap;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ApplicationRunner extends ApplicationAdapter {
	public static final float WORLD_WIDTH = 1280;
	public static final float WORLD_HEIGHT = 960;

		private static final String TAG = ApplicationRunner.class.getSimpleName();

		// 视口世界的宽高统使用 480 * 800, 并统一使用伸展视口（StretchViewport）

		// 舞台
		private Stage stage;

		// 位图字体
		private BitmapFont bitmapFont;

		private Label label;

		@Override
		public void create() {
			// 设置日志输出级别
			Gdx.app.setLogLevel(Application.LOG_DEBUG);

			// 使用伸展视口（StretchViewport）创建舞台
			stage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));

			// 将输入处理设置到舞台（必须设置, 否则文本框无法获取焦点）
			Gdx.input.setInputProcessor(stage);


			// 为了方便演示, 这里直接使用 gdx.jar 中自带的字体文件创建位图字体（只要在 BitmapFont 中包含有的字符才能够被输入）
			bitmapFont = new BitmapFont();

			// gdx.jar 中自带的字体比较小, 这里放大一下
			bitmapFont.getData().setScale(2.0F);


			Label.LabelStyle labelStyle = new Label.LabelStyle();
			labelStyle.font = bitmapFont;
			labelStyle.fontColor = new Color(1, 1, 1, 1);

			label = new Label("a\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\n", labelStyle);

			label.setFontScale(2.0f);
			label.setPosition(0,0);
			label.setHeight(200);
			label.setWidth(200);
			label.setWrap(true);

			stage.addActor(label);

			new ModelBatch();
			new Model();
		}

		@Override
		public void render() {
//			if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
//				Gdx.app.log(TAG, "username = " + usernameTextField.getText());
//				Gdx.app.log(TAG, "password = " + passwordTextField.getText());
//			}

			// 黑色清屏
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			// 更新舞台逻辑
			stage.act();
			// 绘制舞台
			stage.draw();
		}

		@Override
		public void dispose() {
			// 应用退出时释放资源
			if (bitmapFont != null) {
				bitmapFont.dispose();
			}
			if (stage != null) {
				stage.dispose();
			}
		}


	static final String IP_ADDRESS = "192.168.42.129";
	static final Integer PORT = 8022;
	static final String USER_NAME = "u0_a263";
	static final String USER_PASSWORD = "changeme";

	public static void main(String[] args) {
		JSch jSch = new JSch();
		try {
			Session session = jSch.getSession(USER_NAME, IP_ADDRESS, PORT);
			session.setPassword(USER_PASSWORD);
			session.setUserInfo(new CustomeUserInfo());
//			session.connect();

			System.out.println("connection");

			session.connect(30000);   // making a connection with timeout.

			System.out.println("connected, opening channel");

			Channel channel=session.openChannel("shell");

			// Enable agent-forwarding.
			//((ChannelShell)channel).setAgentForwarding(true);

			//channel.connect();


			PipedInputStream inputStreamIn = new PipedInputStream();
			PipedOutputStream inputStreamOut = new PipedOutputStream(inputStreamIn);

			PipedInputStream outputStreamIn = new PipedInputStream();
			PipedOutputStream outputStreamOut = new PipedOutputStream(outputStreamIn);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(outputStreamIn));
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(inputStreamOut);

			channel.setInputStream(inputStreamIn);
			channel.setOutputStream(outputStreamOut);

			channel.connect(3*1000);

			String cmd = null;
			boolean isExit = true;


			Runnable runnable = () -> {
				while(true) {
					Integer s = null;
					try {
						s = bufferedReader.read();

//                        s = inputStreamReader.read();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if(s != null && s != -1){
						System.out.print(new String(Character.toChars(s)));
					}
				}
			};
			new Thread(runnable).start();

			while (isExit){
				if(cmd != null){
					outputStreamWriter.write(cmd);
					outputStreamWriter.flush();
//                    inputStreamIn.read();
					cmd = null;
				}
				isExit = true;
			}
			System.out.println("channel was open");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	}