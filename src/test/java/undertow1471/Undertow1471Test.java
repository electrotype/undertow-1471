package undertow1471;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;

public class Undertow1471Test extends NoAppStartHttpServerTestingBase {

    @Test
    public void webSocketRedirect() throws Exception {

        DefaultWebsocketControllerTest handler = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerConnected(DefaultWebsocketContext context) {
                super.onPeerConnected(context);
                context.sendMessageToCurrentPeer("ok!");
            }
        };
        getRouter().websocket("/two").handle(handler);

        //==========================================
        // The server will redirect calls for "/one"
        // to "/two"
        //==========================================
        getRouter().redirect("/one").to("/two");

        WebsocketClientTest client = new WebsocketClientTest();

        //==========================================
        // This will fail with an Exception!
        //==========================================
        WebsocketClientWriter writer = websocket("/one").disableSslCertificateErrors().connect(client);
        assertNotNull(writer);

        assertTrue(client.waitForStringMessageReceived(1));
        assertEquals("ok!", client.getStringMessageReceived().get(0));
    }


}
