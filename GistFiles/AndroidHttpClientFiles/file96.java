            // Bitmap形式をバイナリに変換
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 100, baos);
            mImageData = baos.toByteArray();
            // AsyncTaskManagerに通信させる
            getLoaderManager().initLoader(0, null, updateAvaterHttpLoaderCallbacks);
