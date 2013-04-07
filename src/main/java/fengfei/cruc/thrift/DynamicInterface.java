package fengfei.cruc.thrift;

import java.util.List;

public class DynamicInterface {
	public DynamicInterface() {

	}

	public DynamicInterface(int maxRetry) {

	}

	static DynamicInterface dynamicInterface = new DynamicInterface();

	public static DynamicInterface get() {
		return dynamicInterface;
	}

	public String test(String interfaceName, List<String> params) {
		return interfaceName;

	}
}
