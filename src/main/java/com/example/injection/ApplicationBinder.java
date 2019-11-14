package com.example.injection;

import com.example.security.CurrentUser;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(JustInTimeServiceResolver.class).to(JustInTimeInjectionResolver.class);
        bind(CurrentUserInjectionResolver.class)
                        .to(new TypeLiteral<InjectionResolver<CurrentUser>>() {})
                        .in(Singleton.class);
    }
}
