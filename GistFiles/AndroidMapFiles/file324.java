// código cliente android afim pegar dados server
WebResource resource = Client.create().resource( SERVER_URI );
MultivaluedMap<String, String> map = new MultivaluedMapImpl();
GenericType<JAXBElement<FileDataList>> listDataType = new GenericType<JAXBElement<FileDataList>>() {};
JAXBElement<FileDataList> jaxbContact = content
	.path("/login/listFiles/")
	.accept(MediaType.APPLICATION_JSON).entity(map)
	.post(listDataType);
System.out.println( jaxbContact.getValue() );


// código server 
@POST @Path("/listFiles")
@Produces( MediaType.APPLICATION_JSON )
public FileDataList getListFiles(MultivaluedMap<String, String> formParams) throws Exception {
	return obterFileDataList(); //
}

// DTO FileDataList
@XmlRootElement(name = "FileList")
public class FileDataList {
	// ... conteudo ... varios atributos
}