package ru.hotel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hotel.Main;
import ru.hotel.domain.HotelUsers;
import ru.hotel.domain.Privilege;
import ru.hotel.repository.PrivilegeRepository;
import ru.hotel.repository.HotelUsersRepository;

import java.util.List;
import java.util.Properties;

import static java.lang.Thread.sleep;

@Service
public class InitUsersService {
    private HotelUsersRepository usersRepository;
    private PrivilegeRepository privilegeRepository;

    public InitUsersService(HotelUsersRepository usersRepository, PrivilegeRepository privilegeRepository) throws InterruptedException {
        this.usersRepository = usersRepository;
        this.privilegeRepository = privilegeRepository;
        initPrivilegesAndUSers();
    }

    public void initPrivilegesAndUSers() throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(Main.class);
        Properties props = new Properties();
        String loginAdmin = new String();
        String passwordAdmin = new String();
        String loginIfti = new String();
        String passwordIfti = new String();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            props.load(classLoader.getResourceAsStream("config.ini"));
            loginAdmin = props.getProperty("LOGINADMIN");
            passwordAdmin = props.getProperty("PASSWORDADMIN");
            loginIfti = props.getProperty("LOGINIFTI");
            passwordIfti = props.getProperty("PASSWORDIFTI");
        }
        catch (Exception e) {
            logger.error("failed to find config.ini");
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);
        String adminpassword = passwordEncoder.encode(passwordAdmin);
        String userpassword = passwordEncoder.encode(passwordIfti);
        Privilege privilege = new Privilege("1", "ROLE_ADMIN");
        Privilege privilege2 = new Privilege("2", "ROLE_USER");
        Privilege privilege3 = new Privilege("100", "HOTEL_READ_PRIVILEGE");
        Privilege privilege4 = new Privilege("101", "HOTEL_WRITE_PRIVILEGE");
        HotelUsers admin = new HotelUsers(loginAdmin, adminpassword, List.of(privilege, privilege3, privilege4));
        HotelUsers user = new HotelUsers(loginIfti, userpassword, List.of(privilege2, privilege3));
        privilegeRepository.deleteAll().subscribe();
        usersRepository.deleteAll().subscribe();
        sleep(1000);
        privilegeRepository.save(privilege).subscribe();
        privilegeRepository.save(privilege2).subscribe();
        privilegeRepository.save(privilege3).subscribe();
        privilegeRepository.save(privilege4).subscribe();
        usersRepository.save(admin).subscribe();
        usersRepository.save(user).subscribe();
    }
}
