package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.socketEntity.SystemMessage;

public interface RealTimeService {
     void remindFriends(ServiceMessage message);
     void remindOtherServers(SystemMessage message);
}
