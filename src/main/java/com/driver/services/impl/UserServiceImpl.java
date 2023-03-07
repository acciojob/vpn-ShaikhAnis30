package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;

    @Autowired
    ServiceProviderRepository serviceProviderRepository3;

    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setConnected(false);
        //now I will assign the countyName
        Country country = new Country();
        if(countryName.equalsIgnoreCase("IND")) {
            country.setCountryName(CountryName.IND);
            country.setCode(CountryName.IND.toCode());
        }else if(countryName.equalsIgnoreCase("USA")) {
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }else if(countryName.equalsIgnoreCase("AUS")) {
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        }else if(countryName.equalsIgnoreCase("JPN")) {
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }else if(countryName.equalsIgnoreCase("CHI")) {
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        }else {
            throw new Exception("Country not found");
        }

        user.setOriginalCountry(country);
        user.setOriginalIp(country.getCode() + "." + userRepository3.save(user).getId());

        userRepository3.save(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

//        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
//        serviceProviderList.add(serviceProvider);
//        user.setServiceProviderList(serviceProviderList);
        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        serviceProviderRepository3.save(serviceProvider);//considered serviceProvider as parent
        return user;
    }
}
