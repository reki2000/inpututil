package util;

import java.util.HashMap;
import java.util.Map;

public class SampleApp extends AppBase {

	public String api(@Param("id") String id, @Param("amount") Integer amount) {
		debug("id:" + id + " amount:" + amount);
		return "ok";
	}

	public String api2(@Param("id") String id, @OptionalParam("amount2") Integer amount) {
		debug("id:" + id + " amount:" + amount);
		return "ok";
	}

	public String api4(@Param("id") Integer[] id) {
		debug("id:" + id[0] + " id:" + id[1]);
		return "ok";
	}

	public static void main(String[] args) {
		SampleApp app = new SampleApp();
		Map<String,Object> map = new HashMap<String,Object>() {{ put("id","ABC"); put("amount", "123"); }};
		app.exec("api", map);
		app.exec("api2", map);

		Map<String,Object> map2 = new HashMap<String,Object>() {{ put("id",new String[] {"36", "123"}); }};
		app.exec("api4", map2);
	}

}
