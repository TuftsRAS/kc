# CORE integration

The configuration settings to enable/disable Core Auth in the Kuali Research monolith. Most of the settings
are for a production environment, there are some that are intended for a development environment only.

## External Configuration settings

### auth.base.url
> Base url for the Core server
>
> **Syntax:** `<schema>://<host>:<port>`\
> **Default:** `https://${application.rest.services.base.url}`\
> **Example:** `http://localhost:3000`

### auth.core.enabled 
>Enable/disable Core Auth 
>
> **Valid Values:** true | false \
> **Default:** false

### auth.filter.allow.admin.proxy 
> Enable/disable proxy login  
>
> **Valid Values:** true | false 

### auth.filter.proxy.username 
> Admin account to use for a proxy login.
>
> **Example:** admin 

### auth.filter.service2service.secret
> Shared secret for service to service communication.
>
> **Example:** secret

### auth.filter.service2service.enabled
> Enables service to service communication.
>
> **Valid Values:** true | false \

### auth.filter.service2service.singleUse
> When enabled service2service tokens are only valid for one service request.
>
> **Valid Values:** true | false \

### auth.filter.service2service.username
> Username for the proxy user used for service2service requests.
>
> **Example:** admin

### auth.rest.urls.regex
> comma separated list of valid auth urls that must be protected via api style security instead of traditional cookie based security.
>
> **default:** `.*/api/v1/.*,.*/api/v2/.*,.*/hr-import/hrimport/.*` 

### auth.system.token
> Auth Token Generated in Core for admin user.
>
> **Default:** <blank>

### auth.user.push.dev.password
> Password used for all users when `auth.user.push.use.dev.password` is set to true. Must be set prior to pushing users to Core.
>
> **Default:** password\
> **Dev Only**
 
### auth.user.push.use.dev.password
> Enable/Disable the use of dev passwords 
>
> **Valid Values:** true | false\
> **Default:** false

### auth.users.url
> Url for the User API for Core. 
> 
> **Example:** `http://localhost:3000/api/v1/users` \
> **Default:** ${auth.base.url}/api/v1/users

### filter.login.class 
> Class used by Kuali to process login requests. To use Core Auth as the login page use `org.kuali.coeus.sys.framework.auth.AuthServiceFilter`
> as the login class.
 
### rice.portal.logout.redirectUrl 
> URL to log out of Kuali with.  Core Auth uses `http://localhost:3000/auth/signout` to log out of the 
> system