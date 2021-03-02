package com.xteq.asc.misc;

import com.jcraft.jsch.UserInfo;

public class CustomeUserInfo implements UserInfo {
		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public String getPassword() {
			return null;
		}

		@Override
		public boolean promptPassword(String message) {
			return false;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return false;
		}

		@Override
		public boolean promptYesNo(String message) {
			return message.contains("The authenticity of host");
		}

		@Override
		public void showMessage(String message) {

		}
	}