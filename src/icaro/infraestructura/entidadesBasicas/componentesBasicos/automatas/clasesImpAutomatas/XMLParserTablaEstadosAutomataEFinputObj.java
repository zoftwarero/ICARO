/*
 *  Copyright 2001 Telefnica I+D. All rights reserved
 */
package icaro.infraestructura.entidadesBasicas.componentesBasicos.automatas.clasesImpAutomatas;

import icaro.infraestructura.entidadesBasicas.NombresPredefinidos;
import java.io.IOException;

import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 *  Clase que convierte un fichero XML en una tabla de estados vlida para un
 *  autmata. Extrae inputs y transiciones para incorporarlos a la tabla de estados
 *
 *@author     Francisco J Garijo
 *@created    20 de septiembre de 2001
 *@modified   20 de Febrero  de 2014
 */
public class XMLParserTablaEstadosAutomataEFinputObj {

	/**
	 * @uml.property  name="document"
	 * @uml.associationEnd  
	 */
	Document document;
    private Logger logger = Logger
			.getLogger(this.getClass().getCanonicalName());
private TablaEstadosAutomataEFinputObjts tablaEstados;
	/**
	 *  Constructor
	 */
	public XMLParserTablaEstadosAutomataEFinputObj() { }


	/**
	 * Analiza el fichero con la descripcion de la tabla y extrae inputs y transiciones
         * Convierte el fichero dado en una tablaEstados y una tabla de transiciones que utilizara el automata
	 *
	 *@param  nombreFich  Nombre del fichero a convertir
	 *@return             Tabla del autmata extrado de ese fichero
	 */
	public TablaEstadosAutomataEFinputObjts extraeTablaEstadosDesdeFicheroXML(String nombreFich){
		 tablaEstados = new TablaEstadosAutomataEFinputObjts();

		// Esta parte es genrica para cualquier parsing XML
		// parsing XML

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		//factory.setNamespaceAware(true);
        URL nombre1;
        InputStream nombre;
		try
		{
		DocumentBuilder builder = factory.newDocumentBuilder();
                Class	clase = this.getClass();
                //nombre1 =  clase.getResource(nombreFich);
                nombre1 =  clase.getResource(nombreFich);
                nombre = clase.getResourceAsStream(nombreFich);
		document = builder.parse(this.getClass().getResourceAsStream(nombreFich));
					//new File(nombreFich));
		}
		catch (SAXException sxe)
		{
			// Error generated during parsing
			Exception x = sxe;
			if (sxe.getException() != null)
			{
				x = sxe.getException();
			}
         System.out.println("Se ha producido un error al procesar el fichero XML: "+nombreFich );
			x.printStackTrace();

		}
		catch (ParserConfigurationException pce)
		{
			// Parser with specified options can't be built
			System.out.println("No se pudo construir un analizador XML con las opciones especificadas referido al fichero XML: "+nombreFich );
			pce.printStackTrace();
		}
		catch (IOException ioe)
		{
			System.out.println("Error de lectura en el fichero XML. Est usted seguro de que el fichero '"+nombreFich+"' est ah?" );
			ioe.printStackTrace();
		}

		// Esta parte es dependiente del tipo del documento que hemos creado

		// ahora tengo el documento XML
		// referencias de ayuda en los recorridos
		org.w3c.dom.Node nodo;
		org.w3c.dom.NamedNodeMap mapaNombreNodo;
		org.w3c.dom.NodeList listaInfoTransiciones;
		String idNodo;

		// capturo el estado inicial (SE QUE SOLO HAY UNO, LAS COMPROBACIONES SE HACEN DESDE EL EXTERIOR)
		org.w3c.dom.NodeList nlInicial = document.getElementsByTagName(NombresPredefinidos.ESTADO_INICIAL);

		nodo = nlInicial.item(0);
		mapaNombreNodo = nodo.getAttributes();
		// este es el identificador del nodo inicial
		idNodo = mapaNombreNodo.getNamedItem(NombresPredefinidos.IDENT_NODO_INICIAL_AUTOMATA_EF).getNodeValue();
		tablaEstados.putEstado(idNodo, NombresPredefinidos.TIPO_ESTADO_INICIAL_AUTOMATA_EF);
		// ahora obtenemos las transiciones desde el estado inicial
		// SE QUE TIENE AL MENOS UNA TRANSICION y que todos los hijos seran transiciones
		listaInfoTransiciones = nodo.getChildNodes();
		procesarListaNodosTransicion(idNodo, listaInfoTransiciones);

		// capturo subestados
		org.w3c.dom.NodeList nlIntermedios = document.getElementsByTagName(NombresPredefinidos.NOMBRE_ESTADO_AUTOMATA_EF);

		// bucle que recorre estados intermedios
		for (int i = 0; i < nlIntermedios.getLength(); i++)
		{
			nodo = nlIntermedios.item(i);
			mapaNombreNodo = nodo.getAttributes();
			// este es el identificador del nodo inicial
			idNodo = mapaNombreNodo.getNamedItem(NombresPredefinidos.IDENT_NODO_INTERMEDIO_AUTOMATA_EF).getNodeValue();
			tablaEstados.putEstado(idNodo, NombresPredefinidos.TIPO_ESTADO_INTERMEDIO_AUTOMATA_EF);

			// SE QUE TIENE AL MENOS UNA TRANSICION y que todos los hijos seran transiciones
			listaInfoTransiciones = nodo.getChildNodes();
			procesarListaNodosTransicion(idNodo, listaInfoTransiciones);
		}

		// capturo estados finales
		org.w3c.dom.NodeList nlFinales = document.getElementsByTagName(NombresPredefinidos.IDENT_NODO_FINAL_AUTOMATA_EF);
		// bucle que recorre estados finales
		for (int i = 0; i < nlFinales.getLength(); i++)
		{
			nodo = nlFinales.item(i);
			mapaNombreNodo = nodo.getAttributes();
			// este es el identificador del nodo inicial
			idNodo = mapaNombreNodo.getNamedItem(NombresPredefinidos.IDENT_NODO_FINAL_AUTOMATA_EF).getNodeValue();
			tablaEstados.putEstado(idNodo, NombresPredefinidos.TIPO_ESTADO_FINAL_AUTOMATA_EF);

			// SE QUE TIENE AL MENOS UNA TRANSICION y que todos los hijos seran transiciones
			listaInfoTransiciones = nodo.getChildNodes();
			procesarListaNodosTransicion(idNodo, listaInfoTransiciones);
		}

      // capturo ahora transiciones universales
  		org.w3c.dom.NodeList nlUniversal = document.getElementsByTagName(NombresPredefinidos.NOMBRE_TRANSICION_UNIVERSAL_AUTOMATA_EF);
		// bucle que recorre transiciones universales
                if (nlUniversal.getLength()>0)
//                    transicion = extraerInfoTransicion(nodo);
//                    
//		for (int i = 0; i < nlUniversal.getLength(); i++)
//		{
//			nodo = nlUniversal.item(i);
//                        
//			mapaNombreNodo = nodo.getAttributes();
//			// extraemos los atributos
//			String input = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_INPUT_AUTOMATA_EF).getNodeValue();
//			String accion = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ACCION_AUTOMATA_EF).getNodeValue();
//                        String estadoSig = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ESTADO_SIGUIENTE_AUTOMATA_EF).getNodeValue();

         tablaEstados.putTransicionUniversal(extraerInfoTransicion(nodo));
//		}

		return tablaEstados;
	}


	/**
	 *  Mtodo auxiliar que procesa las transiciones de un estado determinado
	 *  estado y la anyade a la tabla de estados
	 *@param  idEstado           Estado al que pertenecen las transiciones
	 *@param  listaTransiciones  Lista con las transiciones de ese estado
	 *@param  
	 *      nuevas transiciones que se detectan
	 */
//	private void procesarListaNodosTransicion(String idEstado, org.w3c.dom.NodeList listaTransiciones)
//	{
//		org.w3c.dom.Node nodo;
//		org.w3c.dom.NamedNodeMap mapaNombreNodo;
//
//		for (int i = 0; i < listaTransiciones.getLength(); i++)
//		{
//			nodo = listaTransiciones.item(i);
//			if (nodo.getNodeName().equalsIgnoreCase(NombresPredefinidos.NOMBRE_TRANSICION_AUTOMATA_EF))
//			{
//				mapaNombreNodo = nodo.getAttributes();
//				String input = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_INPUT_AUTOMATA_EF).getNodeValue();
//				String estadoSig = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ESTADO_SIGUIENTE_AUTOMATA_EF).getNodeValue();
//				tablaEstados.addTransicion(idEstado, input,"accionAquitar", estadoSig);
//			}
//		}
//
//	}
        private void procesarListaNodosTransicion (String idEstado,org.w3c.dom.NodeList listaTransiciones) {
		org.w3c.dom.Node nodoTransicion, nodoAux;
		org.w3c.dom.NamedNodeMap mapaNombreNodo;
                String input,accion,estadoSig,modo ;
		for (int i = 0; i < listaTransiciones.getLength(); i++) {
			nodoTransicion = listaTransiciones.item(i);
//			if (nodoTransicion.getNodeName().equalsIgnoreCase(NombresPredefinidos.NOMBRE_TRANSICION_AUTOMATA_EF)) {
//				mapaNombreNodo = nodoTransicion.getAttributes();
//				input = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_INPUT_AUTOMATA_EF)
//						.getNodeValue();
//                                // consideramos la posiblidad de que las acciones no se especificque y las definimos como vacias
//                                nodoAux = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ACCION_AUTOMATA_EF);
//                                if (nodoAux == null)accion=NombresPredefinidos.ACCION_VACIA_AUTOMATA_EF;
//                                 else accion = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ACCION_AUTOMATA_EF).getNodeValue();
//                                estadoSig = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ESTADO_SIGUIENTE_AUTOMATA_EF).getNodeValue();
//                                // consideramos la posiblidad de que la modalidad sea vacia
//                                nodoAux = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_MODO_TRANSICION_AUTOMATA_EF);
//                                if (nodoAux == null) modo = NombresPredefinidos.NOMBRE_MODO_TRANSICION_AUTOMATA_EF;                           
//                                else modo = nodoAux.getNodeValue();
//				tablaEstados.addTransicion(idEstado, input, accion, estadoSig,modo);
                                tablaEstados.putTransicion(idEstado, extraerInfoTransicion(nodoTransicion));
			}
                }

        
         private  TransicionAutomataEF extraerInfoTransicion(org.w3c.dom.Node nodoTransicion) {
            org.w3c.dom.Node  nodoAux;
            org.w3c.dom.NamedNodeMap mapaNombreNodo;
            String identNodo,input,accion,estadoSig,modo ;
            identNodo = nodoTransicion.getNodeName();
            if (identNodo.equalsIgnoreCase(NombresPredefinidos.NOMBRE_TRANSICION_AUTOMATA_EF)|
               (identNodo.equalsIgnoreCase(NombresPredefinidos.NOMBRE_TRANSICION_UNIVERSAL_AUTOMATA_EF))) {
                mapaNombreNodo = nodoTransicion.getAttributes();
                input = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_INPUT_AUTOMATA_EF)
                                .getNodeValue();
                // consideramos la posiblidad de que las acciones no se especificque y las definimos como vacias
                nodoAux = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ACCION_AUTOMATA_EF);
                if (nodoAux == null)accion=NombresPredefinidos.ACCION_VACIA_AUTOMATA_EF;
                 else accion = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ACCION_AUTOMATA_EF).getNodeValue();
                estadoSig = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_ESTADO_SIGUIENTE_AUTOMATA_EF).getNodeValue();
                // consideramos la posiblidad de que la modalidad sea vacia
                nodoAux = mapaNombreNodo.getNamedItem(NombresPredefinidos.NOMBRE_MODO_TRANSICION_AUTOMATA_EF);
                if (nodoAux == null) modo = NombresPredefinidos.NOMBRE_MODO_TRANSICION_AUTOMATA_EF;                           
                else modo = nodoAux.getNodeValue();
                return new TransicionAutomataEF(null, input, accion, estadoSig,modo);
            }else{
            System.out.println("El nodo donde se debe extraer la informacion "+identNodo + "  es incorrecto.: "+ " revisar el fichero del automata" );
            return null;
            }
         }
}
