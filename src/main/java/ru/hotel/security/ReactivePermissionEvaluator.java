package ru.hotel.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Principal;
import java.util.stream.Collectors;

@Component
public class ReactivePermissionEvaluator {
    public boolean hasPermission(Principal principal, String domainObjectClass, Object permission) {
        if ((principal == null) || (domainObjectClass == null) || !(permission instanceof String)){
            return false;
        }
        return hasPrivilege(principal, domainObjectClass.toUpperCase(), permission.toString().toUpperCase());
    }

    public boolean hasPermission(Principal principal, Object targetDomainObject, Object permission) {
        if ((principal == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }
        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

        return hasPrivilege(principal, targetType, permission.toString().toUpperCase());
    }

    public boolean hasPermission(Principal principal, Serializable targetId, String targetType, Object permission) {

        if ((principal == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(principal, targetType.toUpperCase(),
                permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Principal principal, String targetType, String permission) {
        for (GrantedAuthority grantedAuth : ((Authentication) principal).getAuthorities().stream().collect(Collectors.toSet())) {
            if (grantedAuth.getAuthority().startsWith(targetType)) {
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
