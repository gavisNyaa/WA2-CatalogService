# WA2-CatalogService

Email for send and receive:
- email = wa2team01@gmail.com
- password = M!ktgApHDqvt-9sKf4Kh


ENDPOINTS:
* **POST /auth/register** 
  
Needs name, surname, email, password, confirm password and delivery address in the body for register, it sends an email for confirm the registration
* **GET /auth/registrationConfirm** 
  
Requires the token send by email as param for enable the user
* **GET /auth/sendAgain** 
  
With an existing email as param consents to send again the email for the registration confirm if the previous token is expired
* **POST /auth/login** 
  
Needs email and password in the body for login

----
> _Only for login user:_
____
* **POST /user/updateInformation**

Consents to update one or more user information, it accepts, in the body: name, surname, email, delivery address

* **POST /user/updatePassword**

Needs the old password, the new password and confirm password in the body for update the user password

* **GET /user/retrieveInformation**

Without param it sends the basic information of the user: name, surname, email and delivery address

* **POST /user/{email}/enable**

Method only for admin, consents to enable a user with the email in the path variable

* **POST /user/{email}/disable**

Method only for admin, consents to disable a user with the email in the path variable

* **POST /user/{email}/addRole**

Method only for admin, consents to add a role to the user with the email in the path variable

* **POST /user/{email}/removeRole**

Method only for admin, consents to remove a role to the user with the email in the path variable
