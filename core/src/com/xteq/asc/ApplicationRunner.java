package com.xteq.asc;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public class ApplicationRunner extends ApplicationAdapter {
	public static final float WORLD_WIDTH = 1920;
	public static final float WORLD_HEIGHT = 1080;

	private Stage stage;
	private Texture bgTexture;
	Label label;
	TextArea textArea;

	private Texture cursorTexture;

	SpriteBatch batch;
	Texture img;
	BitmapFont bitmapFont = null;

	PipedOutputStream pops;
	PipedInputStream pins;

	private TextField textField;

	byte[] tmp=new byte[1024];
	InputStream in = null;
	Session session = null;
	Channel channel = null;

	PipedInputStream pis = new PipedInputStream(48000);
	PipedOutputStream pos = new PipedOutputStream();
	BufferedWriter bos = new BufferedWriter(new StringWriter());

	@Override
	public void create () {
		bitmapFont = new BitmapFont(Gdx.files.internal("font/fonts.fnt"));

		pops = new PipedOutputStream();
		try {
			pins = new PipedInputStream(pops);
		} catch (IOException e) {
			e.printStackTrace();
		}

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");


		stage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
		Gdx.input.setInputProcessor(stage);

		bgTexture = createBackgroundTexture();
		cursorTexture = createCursorTexture();

		TextField.TextFieldStyle style = new TextField.TextFieldStyle();

		// 设置背景纹理区域
		style.background = new TextureRegionDrawable(new TextureRegion(bgTexture));
		// 设置光标纹理区域
		style.cursor = new TextureRegionDrawable(new TextureRegion(cursorTexture));
		// 设置文本框显示文本的字体来源
		style.font = bitmapFont;
		// 设置文本框字体颜色为白色
		style.fontColor = new Color(1, 1, 1, 1);

		textField = new TextField("ssssssssssss", style);
		textField.setSize(800, 50);
		textField.setPosition(400, 600);

		textField.setAlignment(Align.left);

		label=new Label("124563987258,12456382236874,123654236",
				new Label.LabelStyle(new BitmapFont(), null));
		label.setWidth(100);//设置每行的宽度
		label.setWrap(true);//开启换行
		label.setSize(800, 650);
		label.setAlignment(Align.bottomLeft,Align.left);
		label.setPosition(400, 550);

//		stage.addActor(textArea);
		stage.addActor(label);
		textField.setTextFieldListener(new TextField.TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char c) {
//              if (c == '\n') {
//                  textField.getOnscreenKeyboard().show(true);
//
//              }
				System.out.println("List="+textField.getText());
			}
		});

		stage.addActor(textField);

		JSch jSch = new JSch();
		try {
			session = jSch.getSession("root", "101.132.226.237", 12201);
			session.setPassword("centos");
			session.setUserInfo(new CustomeUserInfo());
			session.connect();
			System.out.println("connection");

			session.connect(30000);   // making a connection with timeout.

			System.out.println("connected, opening channel");

			channel=session.openChannel("shell");

			pis.connect(pos);

			channel.setInputStream(pis);

			channel.setOutputStream(pos);

			//channel.connect();
			channel.connect(3*1000);
		} catch (JSchException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render () {
		try {
			Gdx.gl.glClearColor(0.5F, 0.5F, 0.5F, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			InputStreamReader isr = new InputStreamReader(pis);
			BufferedReader br = new BufferedReader(isr);
			label.setText(br.readLine());

			stage.act();
			stage.draw();
		}catch (Exception e){
			e.printStackTrace();
			dispose();
		}
	}
	
	@Override
	public void dispose () {
		session.disconnect();

		batch.dispose();
		img.dispose();
	}

	/**
	 * 创建文本框的背景纹理
	 */
	private Texture createBackgroundTexture() {
		Pixmap pixmap = new Pixmap(800, 600, Pixmap.Format.RGBA8888);
		pixmap.setColor(1, 1, 1, 1);
		pixmap.drawRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		return texture;
	}

	/**
	 * 创建文本框中的光标纹理
	 */
	private Texture createCursorTexture() {
		Pixmap pixmap = new Pixmap(1, 46, Pixmap.Format.RGBA8888);
		pixmap.setColor(1, 0, 0, 1);
		pixmap.fill();
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		return texture;
	}

	public static void main(String[] args) {
		JSch jSch = new JSch();
		try {
			Session session = jSch.getSession("root", "101.132.226.237", 12201);
			session.setPassword("centos");
			session.setUserInfo(new CustomeUserInfo());
			session.setConfig("StrictHostKeyChecking", "no");
//			session.connect();

			System.out.println("connection");

			session.connect(30000);   // making a connection with timeout.

			System.out.println("connected, opening channel");

			Channel channel=session.openChannel("shell");

			// Enable agent-forwarding.
			//((ChannelShell)channel).setAgentForwarding(true);


				PipedInputStream pis = new PipedInputStream();
				PipedOutputStream pos = new PipedOutputStream(pis);

			channel.setInputStream(pis);

			channel.setOutputStream(System.out);

			//channel.connect();
			channel.connect(3*1000);

			boolean isExit = true;

			while (isExit){
				isExit = true;
			}
			System.out.println("channel was open");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
