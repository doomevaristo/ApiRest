package com.marcosevaristo.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marcosevaristo.business.CidadeAS;
import com.marcosevaristo.model.CidadeEntity;
import com.marcosevaristo.model.dto.EstadoDTO;
import com.marcosevaristo.persistence.CidadeDAO;
import com.marcosevaristo.util.CollectionUtils;
import com.marcosevaristo.util.MensagemUtils;
import com.marcosevaristo.util.StringUtils;

@Path("/")
public class CidadeService {

	private static final Logger LOGGER = Logger.getLogger(CidadeService.class.getName());

	private static final String STATUS = "status";
	private static final String RESPONSE = "response";

	@POST
	@Path("/populaBase")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response populaBaseDeCidades(String jsonStr) {
		Response response;
		
		try {
			JsonObject json = (JsonObject) parse(jsonStr);
			JsonArray cidadesArr = getValue(json, "cidades", JsonArray.class);
			List<CidadeEntity> lCidades = converteJsonArrayEmListCidade(cidadesArr);
			CidadeDAO cidadeDAO = CidadeDAO.getInstance();
			cidadeDAO.populaBaseCidades(lCidades);
			
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.addProperty("mensagem", "sucesso");
			
			response = response(RestResponseStatus.OK, jsonResponse);
		} catch (Exception e) {
			response = tratarErro(e);
		}

		return response;
	}
	
	private List<CidadeEntity> converteJsonArrayEmListCidade(JsonArray jsonArray) throws IOException {
		List<CidadeEntity> lCidades = null;
		if(jsonArray != null) {
			lCidades = new ArrayList<>();
			
			for(int i = 0; i < jsonArray.size(); i++) {
				lCidades.add(parseCidade(jsonArray.get(i).toString()));
			}
		}
		return lCidades;
	}
	
	@POST
	@Path("/recuperaCapitaisOrdenadasPorNome")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recuperaCapitaisOrdenadasPorNome() {
		Response response;
		try {
			List<CidadeEntity> lCapitais = CidadeDAO.getInstance().recuperaCapitaisOrdenadasPorNome();
			
			JsonArray cidadesArr = new JsonArray();
			for(CidadeEntity umaCidadeEntity : lCapitais) {
				cidadesArr.add(converteCidadeEmJsonObject(umaCidadeEntity));
			}
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.add("capitais", cidadesArr);
			
			response = response(RestResponseStatus.OK, jsonResponse);
		} catch (Exception e) {
			response = tratarErro(e);
		}
		
		return response;
	}
	
	@POST
	@Path("/recuperaEstadosMaiorEMenorNumeroCidades")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recuperaEstadosMaiorEMenorNumeroCidades() {
		Response response = null;
		try {			
			List<EstadoDTO> lEstados = CidadeDAO.getInstance().recuperaEstadosMenorEMaiorNroCidades();
			
			if(CollectionUtils.isNotEmpty(lEstados)) {
				JsonArray estadosArr = new JsonArray();
				
				JsonObject umEstadoJson;
				for(EstadoDTO umEstadoDTO : lEstados) {
					umEstadoJson = new JsonObject();
					umEstadoJson.addProperty("nome", umEstadoDTO.getNome());
					umEstadoJson.addProperty("sigla", umEstadoDTO.getSigla());
					estadosArr.add(umEstadoJson);
				}
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.add("estados", estadosArr);
				
				response = response(RestResponseStatus.OK, jsonResponse);
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
		
		return response;
	}
	
	@POST
	@Path("/recuperaQtdCidadesPorEstado")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recuperaQtdCidadesPorEstado() {
		Response response = null;
		try {		
			List<EstadoDTO> lEstados = CidadeDAO.getInstance().recuperaQtdCidadesPorEstado();
			
			if(CollectionUtils.isNotEmpty(lEstados)) {
				JsonArray estadosArr = new JsonArray();
				
				JsonObject umEstadoJson;
				for(EstadoDTO umEstadoDTO : lEstados) {
					umEstadoJson = new JsonObject();
					umEstadoJson.addProperty("nome", umEstadoDTO.getNome());
					umEstadoJson.addProperty("sigla", umEstadoDTO.getSigla());
					umEstadoJson.addProperty("qtdCidades", umEstadoDTO.getQtdCidades());
					estadosArr.add(umEstadoJson);
				}
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.add("estados", estadosArr);
				
				response = response(RestResponseStatus.OK, jsonResponse);
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
			
		return response;
	
	}
	
	@POST
	@Path("/recuperaInfoCidadePorID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recuperaInfoCidadePorID(String jsonStr) {
		Response response = null;
		try {		
			JsonObject json = (JsonObject) parse(jsonStr);
			Long ibgeID = getValue(json, "ibge_id", Long.class);
			if(ibgeID != null) {
				List<CidadeEntity> lCidades = CidadeDAO.getInstance().recuperaCidadePorID(ibgeID);
				if(CollectionUtils.isNotEmpty(lCidades)) {
					JsonArray cidadesArr = new JsonArray();
					for(CidadeEntity umaCidadeEntity : lCidades) {
						cidadesArr.add(converteCidadeEmJsonObject(umaCidadeEntity));
					}
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.add("cidades", cidadesArr);
					
					response = response(RestResponseStatus.OK, jsonResponse);
				} else {
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.add("cidade", null);
					response = response(RestResponseStatus.FAIL, jsonResponse);
				}
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
			
		return response;
	}
	
	@POST
	@Path("/recuperaNomeCidadesPorEstado")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recuperaNomeCidadesPorEstado(String jsonStr) {
		Response response = null;
		try {		
			JsonObject json = (JsonObject) parse(jsonStr);
			String estadoStr = getValue(json, "estado", String.class);
			if(StringUtils.isNotBlank(estadoStr)) {
				List<String> lNomeCidades = CidadeDAO.getInstance().recuperaNomeCidadesPorEstado(estadoStr);
				if(CollectionUtils.isNotEmpty(lNomeCidades)) {
					JsonArray cidadesArr = new JsonArray();
					
					JsonObject umaCidadeJson;
					for(String umNomeCidade : lNomeCidades) {
						umaCidadeJson = new JsonObject();
						umaCidadeJson.addProperty("nome", umNomeCidade);
						cidadesArr.add(umaCidadeJson);
					}
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.add("cidades", cidadesArr);
					
					response = response(RestResponseStatus.OK, jsonResponse);
				}
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
			
		return response;
	}
	
	@POST
	@Path("/insereCidade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insereCidade(String jsonStr) {
		Response response;
		
		try {
			CidadeEntity cidade = parseCidade(jsonStr);
			
			if(cidade != null) {
				CidadeDAO.getInstance().insereCidade(cidade);
				
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.add("cidade", converteCidadeEmJsonObject(cidade));
				response = response(RestResponseStatus.OK, jsonResponse);
			} else {
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.add("cidade", null);
				response = response(RestResponseStatus.FAIL, jsonResponse);
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
			
		return response;
	}
	
	@POST
	@Path("/deletaCidade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletaCidade(String jsonStr) {
		Response response;
		
		try {
			JsonObject cidadeIdJson = (JsonObject) parse(jsonStr);
			
			Long cidadeID = getValue(cidadeIdJson, "ibge_id", Long.class);
			if(cidadeID != null) {
				CidadeDAO.getInstance().deletaCidade(cidadeID);
				
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.addProperty("message", MensagemUtils.CIDADE_DELETADA);
				response = response(RestResponseStatus.OK, jsonResponse);
			} else {
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.addProperty("message", MensagemUtils.ERRO_GENERICO);
				response = response(RestResponseStatus.FAIL, jsonResponse);
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
			
		return response;
	}
	
	@POST
	@Path("/filtraCidade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response filtraCidade(String jsonStr) {
		Response response = null;
		
		try {
			JsonObject json = (JsonObject) parse(jsonStr);
			String campoStr = getValue(json, "campo", String.class);
			String valorStr = getValue(json, "valor", String.class);
			
			if(StringUtils.isNotBlank(campoStr) && StringUtils.isNotBlank(valorStr)) {
				List<CidadeEntity> lCidades = CidadeDAO.getInstance().recuperaCidadesPorFiltroInformado(campoStr, valorStr);
				if(CollectionUtils.isNotEmpty(lCidades)) {
					JsonArray cidadesArr = new JsonArray();
					for(CidadeEntity umaCidadeEntity : lCidades) {
						cidadesArr.add(converteCidadeEmJsonObject(umaCidadeEntity));
					}
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.add("cidades", cidadesArr);
					
					response = response(RestResponseStatus.OK, jsonResponse);
				} else {
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.add("cidade", null);
					response = response(RestResponseStatus.FAIL, jsonResponse);
				}
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.add("cidade", null);
		response = response(RestResponseStatus.FAIL, jsonResponse);
		
		return response;
	}
	
	@POST
	@Path("/recuperaDadosDaColuna")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recuperaDadosDaColuna(String jsonStr) {
		Response response = null;
		
		try {
			JsonObject json = (JsonObject) parse(jsonStr);
			String campoStr = getValue(json, "campo", String.class);
			
			if(StringUtils.isNotBlank(campoStr)) {
				List<String> lDados = CidadeDAO.getInstance().recuperaDadosDaColuna(campoStr);
				if(CollectionUtils.isNotEmpty(lDados)) {
					JsonArray dadosArr = new JsonArray();
					for(String umDado : lDados) {
						dadosArr.add(parse(umDado));
					}
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.add("cidades", dadosArr);
					
					response = response(RestResponseStatus.OK, jsonResponse);
				} else {
					JsonObject jsonResponse = new JsonObject();
					jsonResponse.add("cidade", null);
					response = response(RestResponseStatus.FAIL, jsonResponse);
				}
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.add("cidade", null);
		response = response(RestResponseStatus.FAIL, jsonResponse);
		
		return response;
	}
	
	@POST
	@Path("/recuperaDuasCidadesMaisDistantes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response recuperaDuasCidadesMaisDistantes() {
		Response response = null;
		try {
			CidadeAS cidadeAS = new CidadeAS();
			Map<Double, CidadeEntity[]> mapCidadesMaisDistantes = cidadeAS.recuperaDuasCidadesMaisDistantes();
			
			if(mapCidadesMaisDistantes != null && mapCidadesMaisDistantes.size() > 0) {
				JsonObject jsonResponse = new JsonObject();
				JsonArray cidadesArr = new JsonArray();
				for(Double umaDistancia : mapCidadesMaisDistantes.keySet()) {
					cidadesArr.add(converteCidadeEmJsonObject(mapCidadesMaisDistantes.get(umaDistancia)[0]));
					cidadesArr.add(converteCidadeEmJsonObject(mapCidadesMaisDistantes.get(umaDistancia)[1]));
					jsonResponse.add("cidades", cidadesArr);
					jsonResponse.add("distancia", parse(umaDistancia.toString()));
				}
			} else {
				JsonObject jsonResponse = new JsonObject();
				jsonResponse.add("cidades", null);
				jsonResponse.add("distancia", null);
				response = response(RestResponseStatus.FAIL, jsonResponse);
			}
		} catch (Exception e) {
			response = tratarErro(e);
		}
		
		return response;
	}
	
	private JsonObject converteCidadeEmJsonObject(CidadeEntity cidade) {
		JsonObject jsonObject = null;
		if(cidade != null) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("ibge_id", cidade.getIbge_id());
			jsonObject.addProperty("uf", cidade.getUf());
			jsonObject.addProperty("name", cidade.getName());
			jsonObject.addProperty("capital", cidade.isCapital());
			jsonObject.addProperty("lon", cidade.getLon());
			jsonObject.addProperty("lat", cidade.getLat());
			jsonObject.addProperty("no_accents", cidade.getNo_accents());
			jsonObject.addProperty("alternative_names", cidade.getAlternative_names());
			jsonObject.addProperty("microregion", cidade.getMicroregion());
			jsonObject.addProperty("mesoregion", cidade.getMesoregion());
		}
		
		return jsonObject;
	}

	public Response response(RestResponseStatus status, JsonObject json) {
		JsonObject jsonResponse = new JsonObject();

		jsonResponse.addProperty(STATUS, status.name());
		jsonResponse.add(RESPONSE, json);

		return responseRoot(status, jsonResponse);
	}
	
	public Response response(RestResponseStatus status, String response) {
		JsonObject jsonResponse = new JsonObject();

		jsonResponse.addProperty(STATUS, status.name());
		jsonResponse.addProperty(RESPONSE, response);

		return responseRoot(status, jsonResponse);
	}

	public Response tratarErro(Exception e) {
		return response(RestResponseStatus.ERROR, e.getClass().getName() + ": " + e.getMessage());
	}
	
	private Response responseRoot(RestResponseStatus status, JsonObject json) {
		String entity = json.toString();

		ResponseBuilder builder = Response.ok();
		try {
			builder.header("Content-Length", entity.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		// builder.header("Content-MD5", StringUtils.hashMD5(entity));
		builder.entity(entity);
		return builder.build();
	}

	public static JsonElement parse(String jsonStr) {
		return new JsonParser().parse(jsonStr);
	}
	
	public static CidadeEntity parseCidade(String jsonStr) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonStr, CidadeEntity.class);
	}
	
	public static Long parseCidadeID(String jsonStr) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(jsonStr, Long.class);
	}

	public static <T> T getValue(JsonObject jsonObject, String key, Class<T> clazz) throws ParseException {
		JsonElement jsonElement = jsonObject.get(key);
		Object value = jsonElement;

		if (Date.class.equals(clazz) || Number.class.isAssignableFrom(clazz) || String.class.equals(clazz)) {
			if (jsonElement != null) {
				value = jsonElement.getAsString();
			}
		}

		return getValue(value, clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object object, Class<T> clazz) throws ParseException {
		if (object != null) {
			if (Date.class.equals(clazz)) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				return (T) sdf.format((Date) object);
			} else if (Integer.class.equals(clazz)) {
				return (T) Integer.valueOf(object.toString());
			} else if (Long.class.equals(clazz)) {
				return (T) Long.valueOf(object.toString());
			} else if (Float.class.equals(clazz)) {
				return (T) Float.valueOf(object.toString());
			} else if (Double.class.equals(clazz)) {
				return (T) Double.valueOf(object.toString());
			} else if (Boolean.class.equals(clazz)) {
				return (T) Boolean.valueOf(object.toString());
			} else if (String.class.equals(clazz)) {
				return (T) object.toString();
			}

			return (T) object;
		}
		return null;
	}

	public enum RestResponseStatus {
		OK, ERROR, CONFIRMED, SUCESS, FAIL
	}
}
