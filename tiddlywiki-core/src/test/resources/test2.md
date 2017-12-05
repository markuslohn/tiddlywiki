---
title: Secure Servicebus Projects by using a Policy Set
keywords: [Oracle SOA, Servicbus, Security, OWSM, Fusion Middleware]
author: mustermann
---

# Secure Servicebus Projects by using a Policy Set

Every webservice reachable outside the network must be secured at a minimum with username and password. The security will be activated and controlled by Web Service Manager. The security can be enabled with configuration only, no programming required.

Afterwards are described the necessary steps to secure the servicebus projects from outside.

1. Configure Oracle HTTP Server
2. Prepare Servicebus projects
2. Configure Policy Set
3. Create osb user in WebLogic Security Realm


## Distinct Security Settings for Internal and External Requests to Oracle SOA

In a Policy Set there can be specified a constraint specifying when to apply the policies. The constraint
can validate a header property in the request.

```
HTTPHeader("VIRTUAL_HOST_TYPE","External") - Sets the constraint as external and indicates that the policy set should apply to all external requests received through Oracle HTTP Server.

!HTTPHeader("VIRTUAL_HOST_TYPE","External") - Sets the constraint as NOT external and indicates that the policy set should apply to all incoming requests not received through Oracle HTTP server, such as those from an internal network.
```

This feature works only in conjunction with Oracle HTTP Server. It allows to distinct the security between internal and external requests to Oracle SOA.

### [Configuring the Oracle HTTP Server to Specify the Request Origin](https://docs.oracle.com/middleware/12213/owsm/security/GUID-13F43DB4-C837-42C2-B6C5-6D6E07266415.htm#GUID-42BCAE2E-CDD7-416C-86C0-40521FD6A946)

1. Verify that the module mod_headers is loaded.

   ```
    /u01/app/oracle/ohs/domains/ohs_domain/config/fmwconfig/components/OHS/ohs1
   ```

2. Open file `/u01/app/oracle/ohs/domains/ohs_domain/config/fmwconfig/components/OHS/ohs1/moduleconf/test_soainternal_vh.conf`and add request header for internal:

    ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-8.PNG)

3. Open file `/u01/app/oracle/ohs/domains/ohs_domain/config/fmwconfig/components/OHS/ohs1/moduleconf/test_soa_vh.conf` and add request header for external:

    ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-9.PNG)

4. Restart HTTP Server

## Prepare Servicebus projects

1. Activate option "From OWSM Policy Store" in every Proxy Service. Do not select any policy!

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-1.PNG)


## Configure Policy Set

1. Define a Policy Set for all Servicebus projects in Enterprise Manager

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-7.PNG)

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-6.PNG)

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-5.PNG)

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-4.PNG)

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-10.PNG)

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-3.PNG)

   ![](../_static/2017-11-28_oracle-servicebus-security-policy-sets-2.PNG)

## Create user osb in WebLogic Security Realm

The user osb will be used for authentication when calling a secured servicebus project.

## References

- [	How to Secure an Oracle Service Bus ( OSB ) Service with an OWSM Policy ? (Doc ID 1265548.1)](https://support.oracle.com/epmos/faces/DocumentDisplay?_afrLoop=443851409625099&parent=DOCUMENT&sourceId=2318458.1&id=1265548.1&_afrWindowMode=0&_adf.ctrl-state=ck7i3ghsh_208)
