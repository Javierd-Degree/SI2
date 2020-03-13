Javier Delgado del Cerro y Javier López Cano

# Memoria P1B

### Cuestión 1





### Ejercicio 1

En primer lugar añadimos el siguiente import en la parte superior del fichero VisaDAOBean.java junto con el resto de los imports:

```java
import javax.ejb.Stateless;
```

Cambiamos la declaración de la clase VisaDAOBean por:

```java
@Stateless(mappedName="VisaDAOBean")
public class VisaDAOBean extends DBTester implements VisaDAOLocal
```

De modo que emplee la anotación `@Stateless` en lugar de `@WebService` e implemente la interfaz `VisaDAOLocal`.

Eliminamos el constructor por defecto de la clase, como se nos indica y ajustamos los métodos a la interfaz, para ello eliminamos todas las anotaciones `@WebMethod` en todos los métodos, y se debe cambiar el retorno del método `getPagos()` de `ArrayList<PagoBean>` a `PagoBean[]` realizando además los cambios necesarios en el método.

De este modo las cabeceras de los métodos modificados quedarán:

```java
public boolean compruebaTarjeta(TarjetaBean tarjeta);
public synchronized PagoBean realizaPago(PagoBean pago);
public PagoBean[] getPagos(String idComercio);
public int delPagos(String idComercio);
public boolean isPrepared();
public void setPrepared(boolean prepared);
public boolean isDebug();
public void setDebug(boolean debug);
public void setDebug(String debug);
public boolean isDirectConnection();
public void setDirectConnection(boolean directConnection);
```



### Ejercicio 2

Tras cambiar los imports que se nos piden, el inicio de `ProcesaPago.java` queda de la siguiente forma:

![](./Ej2_1.png)

Tras esto añadimos el objeto proxy para acceder al EJB local con la anotación `@EJB` y .

![](./Ej2_2.png)

Por último eliminamos la declaración del webservice *VisaDAOWS* y el código para obtener el objeto remoto pues se usará el EJB local, además eliminamos también las referencias a BindingProvider. Por tanto el código quedará del siguiente modo:

![](./Ej2_3.png)



### Cuestión 2



### Ejercicio 3

Editamos el fichero `build.properties` colocando en `as.host.client` y `as.host.server` la dirección IP `10.1.2.2`, es decir, la IP del servidor de aplicaciones, esto se debe a que tanto el cliente como el servidor van a desplegarse en la misma máquina virtual.

En el fichero `postgresql.properties` introducimos en el parámetro `db.client.host` la IP `10.1.2.2`, ya que en esta IP estará desplegado el servidor, que es quien accederá a la base de datos , y en el parámetro `db.host` la IP `10.1.2.1` ya que es la IP de la máquina virtual en que estará la base de datos.

Tras esto desplegamos el cliente, el servidor y la aplicación con los comandos:

```bash
$ ant compilar-servidor
$ ant empaquetar-servidor
$ ant compilar-cliente
$ ant empaquetar-cliente
$ ant empaquetar-aplicacion
$ ant desplegar
```

Y al abrir la pestaña de administración de Glassfish y vemos que se ha desplegado como `Enterprise Application`.

![](./Ej3.png)



### Ejercicio 4

Accedemos a la aplicación para comprobar su correcto funcionamiento.

Realizamos un pago mediante `pago.html` y vemos que se realiza correctamente.

![](./Ej4_1.png)

Realizamos ahora uno a través de `testbd.jsp`, sin *directconnection* y vemos de nuevo que funciona correctamente.

![](./Ej4_2.png)

Listamos ahora los pagos del comercio 1 para ver que se han guardado en la base de datos correctamente:

![](./Ej4_3.png)

Por último eliminamos los pagos y vemos que la acción se ejecuta sin problemas.

![](./Ej4_4.png)

Como todas estas acciones han funcionado como se esperaba, deducimos que la aplicación se ha desplegado de forma correcta.



### Ejercicio 5

Realizamos los cambios necesarios en el código para implementar la invocación remota de los métodos de los EJB.

Primero creamos la clase *VisaDAORemote* copiando *VisaDAOLocal* y cambiando el nombre de la interfaz a *VisaDAORemote* y la anotación `@Local` a `@Remote` para que implemente la invocación remota. Para esto es también necesario realizar el import de `Remote` de `java.ejb`. El código por tanto quedará:

![](./Ej5_VisaDAORemote.png)

Tras esto modificamos *VisaDAOBean* para que implemente también la interfaz *VisaDAORemote*:

![](./Ej5_VisaDAOBean.png)

Por último modificamos *TarjetaBean* y *PagoBean* para que implementen la interfaz `Serializable` realizando el import correspondiente.

De este modo *PagoBean* quedaría:

![](./Ej5_PagoBean.png)

Y *TarjetaBean*:

![](./Ej5_TarjetaBean.png)



### Ejercicio 6

Para implementar el cliente remoto, partimos de `P1-base` y eliminamos el directorio `ssii2/visa/dao` como se nos indica.

Modificamos *PagoBean* y *TarjetaBean* para que implementen la interfaz `Serializable` del mismo modo que en el ejercicio 5.

Copiamos la interfaz `VisaDAORemote` implementada en el servidor remoto en el ejercicio 5, y la pegamos en el cliente remoto en el directorio `P1-ejb-cliente-remoto/src/ssii2/visa` como se nos indica en el enunciado.

En los servlets eliminamos las declaraciones de `VisaDAO dao` y las sustituimos por referencias al objeto remoto EJB mediante la anotación `@EJB` del mismo modo que en el ejercicio 2, pero con el objeto remoto en lugar del local, realizando tambien los imports necesarios.

Creamos el fichero `glassfish-web.xml` en `web/WEB-INF` e introducimos en este las líneas que se nos indican en el enunciado que especifican las referencias a los EJBs remotos, introduciendo en el lugar adecuado la IP del servidor remoto, que en nuestro caso es `10.1.2.2`.

Desplegamos el cliente en la máquina virtual con IP `10.1.2.1` y realizamos un pago a través de `pago.html`.

![](./Ej6.png)

Como se observa obtenemos el mensaje indicando que el pago se ha realizado de forma correcta y, por tanto, deducimos que el cliente está correctamente implementado y desplegado.

### Ejercicio 7

En primer lugar, añadimos el atributo *saldo* y sus métodos de acceso a *TarjetaBean.java*:

![](./Ej7_TarjetaBean.png)

Tras esto, modificamos el archivo *VisaDAOBean.java* para importar *EJBException*, y declaramos los dos *prepared statements* pedidos:

![](./Ej7_1.png)

![](./Ej7_Prep_Statements.png)

Modificamos entonces el método *realizaPago*, que queda de la siguiente manera:

![](Ej7_RealizaPago1.png)

![](Ej7_RealizaPago2.png)

![](Ej7_RealizaPago3.png)

Tras esto, modificamos el servlet *ProcesaPago* para que capte la nueva posible interrupción *EJBException*:

![](./Ej7_ProcesaPago.png)



### Ejercicio 8

En primer lugar, probamos a hacer un pago correcto y apreciamos en Tora como se ha actualizado el saldo:

![](Ej8_1.png)![](Ej8_2.png)

Tras esto, probamos a hacer una operación con id de transacción y comercio duplicados y vemos que el saldo no varía, como era de esperar:

![](Ej8_3.png)

![](Ej8_4.png)



### Ejercicio 9

Declaramos en la máquina *10.1.2.2* la factoría de conexiones como se indica:

![](Ej9.png)



### Ejercicio 10

De nuevo, declaramos la cola de mensajes en la máquina *10.1.2.2* como se indica en el enunciado:

![](Ej10.png)



### Ejercicio 11



### Ejercicio 12

La ventaja de usar el método basado en recursos JMS dinámicos en vez de en estáticos es principalmente que al poder establecer los nombres de la connection factory y de la cola en tiempo de ejecución, puedes usar un servidor externo para obtener dichos nombres, de forma que te permite por ejemplo añadir más colas sin modificar la aplicación, redistribuyendo los clientes.

Las modificaciones hechas en el archivo son las siguientes:

![](./Ej12_1.png)

![](./Ej12_2.png)

### Ejercicio 13

Añadimos a los campos *as.host.mdb* y *as.host.server* la IPs *10.1.2.2* porque es la máquina en la que se encuentra servidor y las colas de mensajes.

Revisando el fichero *jms.xml* podemos ver que para crear la cola JMS se utiliza:
```xml
<antcall target="create-jms-resource">
   <param name="jms.restype" value="javax.jms.Queue" />
   <param name="jms.resource.property" value="Name=${jms.physname}" />
   <param name="jms.resource.name" value="${jms.name}" />
</antcall>


 <target name="create-jms-resource"
     description="creates jms destination resource">
     <exec executable="${asadmin}">
        <arg line=" --user ${as.user}" />
        <arg line=" --passwordfile ${as.passwordfile}" />
        <arg line=" --host ${as.host.server}" />
        <arg line=" --port ${as.port}" />
        <arg line="create-jms-resource"/>
        <arg line=" --restype ${jms.restype}" />
        <arg line=" --enabled=true" />
        <arg line=" --property ${jms.resource.property}" />
        <arg line=" ${jms.resource.name}" />
     </exec>
 </target>

```

Por tanto, el comando para crear la cola sería
```bash
asadmin --user admin --paswordfile ./passwordfile --host 10.1.2.2 --port 4848 create-jms-resource --restype javax.jms.Queue --enabled=true --property Name=VisaPagosQueue jms/VisaPagosQueue
```

### Ej14


Una vez ejecutamos `/opt/glassfish-4.1.2/glassfish4/glassfish/bin/appclient -targetserver 10.1.2.2 -client dist/clientjms/P1-jms-clientjms.jar 0` para enviar a la cola el id de transacción cero, la salida con `/opt/glassfish-4.1.2/glassfish4/glassfish/bin/appclient -targetserver 10.1.2.2 -client dist/clientjms/P1-jms-clientjms.jar -browse` es la siguiente:

Esto demuestra que el mensaje se ha enviado correctamente.