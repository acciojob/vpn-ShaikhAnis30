package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        ServiceProvider serviceProvider = new ServiceProvider();
        Admin admin = adminRepository1.findById(adminId).get();
        serviceProvider.setId(admin.getId());
        serviceProvider.setName(providerName);

        serviceProviderRepository1.save(serviceProvider);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {
        Country country = new Country();
        //country.setId(serviceProviderId);
//        country.setCountryName(CountryName.valueOf(countryName));
//        country.setCode(CountryName.valueOf(countryName).toCode());
        if(countryName.equals("IND")) {
            country.setCountryName(CountryName.IND);
            country.setCode(CountryName.IND.toCode());
        }else if(countryName.equals("USA")) {
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }else if(countryName.equals("AUS")) {
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        }else if(countryName.equals("JPN")) {
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }else if(countryName.equals("CHI")) {
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        }else {
            throw new Exception("Country not found");
        }

        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        List<Country> countryList = serviceProvider.getCountryList();
        countryList.add(country);
        serviceProvider.setCountryList(countryList);

        serviceProviderRepository1.save(serviceProvider);
//        countryRepository1.save(country);
        return serviceProvider;
    }
}
