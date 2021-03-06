package com.example.injection;

import com.example.business.User;
import com.example.security.CurrentUser;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

public class CurrentUserInjectionResolver implements InjectionResolver<CurrentUser> {

	private final javax.inject.Provider<SecurityContext> securityContextProvider;

	@Inject
	public CurrentUserInjectionResolver(
		javax.inject.Provider<SecurityContext> securityContextProvider) {
		this.securityContextProvider = securityContextProvider;
	}

	@Override
	public Object resolve(Injectee injectee, ServiceHandle<?> sh) {
		if (User.class == injectee.getRequiredType()) {
			return securityContextProvider.get().getUserPrincipal();
		}
		return null;
	}

	@Override
	public boolean isConstructorParameterIndicator() { return false; }

	@Override
	public boolean isMethodParameterIndicator() { return false; }
}
