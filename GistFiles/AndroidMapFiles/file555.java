package net.multiplemonomials.densetsu.xml;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.bea.xml.stream.events.EntityReferenceEvent;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javay.xml.stream.XMLEventReader;
import javay.xml.stream.XMLInputFactory;
import javay.xml.stream.XMLStreamException;
import javay.xml.stream.events.Attribute;
import javay.xml.stream.events.XMLEvent;

/**
 * AsyncTask to add the JMDict file to the database.
 *
 * Takes the XML file and the database object as parameters.
 */
public abstract class PullParserAsyncTask<Params, Progress, Retval> extends AsyncTask<Params, Progress, Retval>
{


	protected XMLEvent event;
	protected XMLEventReader reader;

	protected String endElementName;
	protected String startElementName;
	protected String elementText;
	protected HashMap<String, String> attributes = new HashMap<String, String>(); //contains attributes of current start element tag.  Namespaces are removed.

	protected PullParserAsyncTask(InputStream xmlStream) {
		try
		{

			XMLInputFactory factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);

			reader = factory.createXMLEventReader(xmlStream);
		}
		catch(XMLStreamException ex) //can't catch exceptions from a super constructor in a subclass, so we have to handle them here
		{
			Log.e("PullParserAsyncTask", "Error creating XML reader: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}


	protected XMLEvent advanceParser() throws XMLStreamException
	{
		do
		{
			event = reader.nextEvent();
		}
		while(!(event.isStartElement() || (event.isCharacters() && !event.asCharacters().isWhiteSpace()) || event.isAttribute() || event.isEndDocument() || event.isEndElement() || event.isEntityReference()));

		startElementName = null;
		endElementName = null;
		elementText = null;
		attributes.clear();

		if(event.isStartElement())
		{
			startElementName = event.asStartElement().getName().getLocalPart();

			Iterator<Attribute> attributesIterator = (Iterator<Attribute>)event.asStartElement().getAttributes();
			while(attributesIterator.hasNext())
			{
				Attribute attr = attributesIterator.next();
				attributes.put(attr.getName().getLocalPart(), attr.getValue());
			}
		}
		if(event.isEndElement())
		{
			endElementName = event.asEndElement().getName().getLocalPart();
		}
		else if(event.isCharacters())
		{
			elementText = event.asCharacters().getData();
		}

		else if(event instanceof EntityReferenceEvent)
		{
			//treat these just like text
			//for now, we ignore the replacement and just focus on the entity code, as that's what's used in the database.
			elementText = ((EntityReferenceEvent)event).getName();
		}

		return event;
	}

}

// USAGE EXAMPLE: (put this in your doInBackground() method)

while(!advanceParser().isEndDocument())
{
	if("header".equals(startElementName))
	{
		do
		{
			advanceParser();
			if("database_version".equals(startElementName))
			{
				advanceParser();
				databaseVersion = elementText;
			} else if("file_version".equals(startElementName))
			{
				advanceParser();
				if(!"4".equals(elementText))
				{
					throw new RuntimeException("Schema version wrong! Was: " + String.valueOf(elementText) + ", expected: 4");
				}
			}
		}
		while(!"header".equals(endElementName));
		
		if("codepoint".equals(startElementName))
		{
			while(!"codepoint".equals(endElementName))
			{
				advanceParser();
				if("cp_value".equals(startElementName))
				{
					if(attributes.containsKey("cp_type"))
					{
						String codepointType = attributes.get("cp_type");
					}
					String codepointValue = elementText;
					advanceParser();
				}

			}

		}
	}
}
