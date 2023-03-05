package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;

    @Autowired
    ServiceProviderRepository serviceProviderRepository2;

    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(user.getConnected()) throw new Exception("Already connected");

        else if(countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())) {
            return user;
        }

        else {
            if (user.getServiceProviderList() == null) throw new Exception("Unable to connect");

            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();

            ServiceProvider dummyServiceProvider = null;
            int id = Integer.MAX_VALUE;
            Country dummyCountry = null;
            for (ServiceProvider serviceProvider : serviceProviderList) { //this service provider
                List<Country> countryList = serviceProvider.getCountryList(); //serving these countries
                for (Country country : countryList) {
                    if (countryName.equalsIgnoreCase(country.getCountryName().toString()) && serviceProvider.getId() < id) {
                        id = serviceProvider.getId();
                        dummyServiceProvider = serviceProvider;
                        dummyCountry = country;
                    }
                }
            }

            if (dummyServiceProvider != null) {
                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(dummyServiceProvider);

                user.getConnectionList().add(connection);
                dummyServiceProvider.getConnectionList().add(connection);
                user.setMaskedIp(dummyCountry.getCode() + "." + dummyServiceProvider.getId() + "." + userId);
                user.setConnected(true);

                userRepository2.save(user);
                serviceProviderRepository2.save(dummyServiceProvider);
            }
        }
        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(!user.getConnected()) throw new Exception("Already disconnected");

        user.setMaskedIp(null);
        user.setConnected(false);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

        if(receiver.getConnected()) {
            //I will fetch country form maskIp
            String countryCode = receiver.getMaskedIp().substring(0,3);

            if(sender.getOriginalCountry().getCode().equals(countryCode))
                return sender; //they can communicate
            else {
                //now I have to connect sender country to receiver country
                //for that I have to get the country name of receiver
                String countryNameOfReceiver = "";
                if(countryCode.equals("001"))
                    countryNameOfReceiver = CountryName.IND.toString();
                else if(countryCode.equals("002"))
                    countryNameOfReceiver = CountryName.USA.toString();
                else if(countryCode.equals("003"))
                    countryNameOfReceiver = CountryName.AUS.toString();
                else if(countryCode.equals("004"))
                    countryNameOfReceiver = CountryName.CHI.toString();
                else if(countryCode.equals("005"))
                    countryNameOfReceiver = CountryName.JPN.toString();

                sender = connect(senderId, countryNameOfReceiver);
                if(!sender.getConnected()) throw new Exception("Cannot establish communication");
                else return sender;
            }

        }else{
            String receiverCountryName = receiver.getOriginalCountry().getCountryName().toString();
            if(receiverCountryName.equals(sender.getOriginalCountry().getCountryName().toString()))
                return sender;
            else {
                sender = connect(senderId, receiverCountryName);
                if(!sender.getConnected()) throw new Exception("Cannot establish communication");
                else return sender;
            }
        }
    }
}
