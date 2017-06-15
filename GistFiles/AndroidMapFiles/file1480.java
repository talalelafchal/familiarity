/**
 * Created by marcel on 08/04/15.
 */
public abstract class BaseModel<T> implements Serializable {

  private int code;
  private ErrorBody errorBody;
  private long createdTime;

  public BaseModel() {
  }

  public BaseModel(int code) {
    this.code = code;
  }

  public static <T extends BaseModel<T>> T getInstance(Class<T> clazz, int code) {
    return BaseModel.getInstance(clazz, code, null);
  }

  public static <T extends BaseModel<T>> T getInstance(Class<T> clazz, int code,
      ErrorBody errorBody) {
    try {
      T instance = clazz.newInstance();
      instance.setCode(code);
      instance.setCreatedTime(System.currentTimeMillis());
      instance.setErrorBody(errorBody);
      return instance;
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(long createdTime) {
    this.createdTime = createdTime;
  }

  public ErrorBody getErrorBody() {
    return errorBody;
  }

  public void setErrorBody(ErrorBody errorBody) {
    this.errorBody = errorBody;
  }
}