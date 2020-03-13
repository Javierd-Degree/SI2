/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * VisaCancelacionJMSBean.java
 */

package ssii2.visa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.util.logging.Logger;

/**
 * @author jaime
 */
@MessageDriven(mappedName = "jms/VisaPagosQueue")
public class VisaCancelacionJMSBean extends DBTester implements MessageListener {
  static final Logger logger = Logger.getLogger("VisaCancelacionJMSBean");
  @Resource
  private MessageDrivenContext mdc;

  private static final String UPDATE_CANCELA_QRY = null;
   // TODO : Definir UPDATE sobre la tabla pagos para poner
   // codRespuesta a 999 dado un código de autorización


  public VisaCancelacionJMSBean() {
  }

  private static final String UPDATE_RESPUESTA_CODE =
                "update pagos" +
                " set codRespuesta = 999" +
                " where idAutorizacion = ?" ;

  private static final String UPDATE_SALDO_QRY =
                  "update tarjeta inner join pagos on tarjeta.numeroTarjeta=pagos.numeroTarjeta" +
                  " set tarjeta.saldo = tarjeta.saldo + pagos.importe" +
                  " where pagos.idAutorizacion = ?" ;

  // TODO : Método onMessage de ejemplo
  // Modificarlo para ejecutar el UPDATE definido más arriba,
  // asignando el idAutorizacion a lo recibido por el mensaje
  // Para ello conecte a la BD, prepareStatement() y ejecute correctamente
  // la actualización
  public void onMessage(Message inMessage) {
      TextMessage msg = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      Connection con = null;

      try {
          if (inMessage instanceof TextMessage) {
              msg = (TextMessage) inMessage;
              logger.info("MESSAGE BEAN: Message received: " + msg.getText());

              con = getConnection();

              pstmt = null;
              String update = UPDATE_RESPUESTA_CODE;

              pstmt = con.prepareStatement(update);
              pstmt.setString(1, msg.getText());
              rs = pstmt.executeQuery();

              update = UPDATE_SALDO_QRY;

              pstmt = con.prepareStatement(update);
              pstmt.setString(1, msg.getText());
              rs = pstmt.executeQuery();

          } else {
              logger.warning(
                      "Message of wrong type: "
                      + inMessage.getClass().getName());
          }
      } catch (JMSException e) {
          e.printStackTrace();
          mdc.setRollbackOnly();
      } catch (Throwable te) {
          te.printStackTrace();
      } finally {
            try {
                if (rs != null) {
                    rs.close(); rs = null;
                }
                if (pstmt != null) {
                    pstmt.close(); pstmt = null;
                }
                if (con != null) {
                    closeConnection(con); con = null;
                }
            } catch (SQLException e) {
            }
        }
  }


}
