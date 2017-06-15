String ip = "";
				
				try {
				      for (Enumeration<NetworkInterface> en = NetworkInterface
				          .getNetworkInterfaces(); en.hasMoreElements();) {
				        NetworkInterface intf = en.nextElement();
				        for (Enumeration<InetAddress> enumIpAddr = intf
				            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
				          InetAddress inetAddress = enumIpAddr.nextElement();
				          if (!inetAddress.isLoopbackAddress()) {
				            ip = inetAddress.getHostAddress().toString();
				          }
				        }
				      }
				    } catch (SocketException ex) {
				      ex.printStackTrace();
				    }