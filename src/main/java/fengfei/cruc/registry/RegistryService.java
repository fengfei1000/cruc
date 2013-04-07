package fengfei.cruc.registry;

import java.net.URL;
import java.util.List;

public interface RegistryService {

	void register(Service service);

	void unregister(Service service);

	void subscribe(Service service, Notification notification);

	void unsubscribe(Service service, Notification notification);

	List<URL> lookup(Service service);

}
