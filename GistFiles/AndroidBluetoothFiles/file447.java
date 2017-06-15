
public Observable<byte[]> readFile(@NonNull FileInputStream stream) {
  final SyncOnSubscribe<FileInputStream, byte[]> fileReader = SyncOnSubscribe.createStateful(
    () -> stream,
    (stream, output) -> {
      try {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int count = stream.read(buffer);
        if (count < 0) {
          output.onCompleted();
        } else {
          output.onNext(buffer);
        }
      } catch (IOException error) {
        output.onError(error);
      }
      return stream;
    },
    s -> IOUtil.closeSilently(s));
  return Observable.create(fileReader);
}