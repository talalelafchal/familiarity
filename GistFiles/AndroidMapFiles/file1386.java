private String JsonEncode(Map<String, Object> map){
    ObjectMapper mapper = new ObjectMapper();
    String ret = null;
    try {
      StringWriter strWriter = new StringWriter();
      mapper.writeValue(strWriter, map);
      ret = strWriter.toString();
    } catch (JsonGenerationException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
}