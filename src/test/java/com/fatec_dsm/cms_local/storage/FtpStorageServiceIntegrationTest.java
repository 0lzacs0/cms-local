package com.fatec_dsm.cms_local.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FtpStorageServiceIntegrationTest {
    // Requer o dublê local: tools/ftp-server → npm start (127.0.0.1:2121) _ Requires the local double: tools/ftp-server → npm start (127.0.0.1:2121)
    // Credenciais descartáveis do server.js — dublês de teste, incluídos no repositório intencionalmente _ Throwaway credentials from server.js — test doubles, committed on purpose
    private final FtpStorageService service =
        new FtpStorageService("127.0.0.1", 2121, "cms_test", "senhaTest123");

    @Test
    void opensAndClosesSession() {
        try (StorageSession session = service.open()) {
            assertNotNull(session);
            assertFalse(session.list("/").isEmpty());   //canais de controle e dados ativos _ control + data channels alive
        }// close() foi executado aqui — desconexão no fluxo _ // close() ran here — disconnect on path
    }

    @Test
    void wrongPasswordIsRejectedWithReplyCode(){
        FtpStorageService bad =
                new FtpStorageService("127.0.0.1", 2121,"cms_test", "definitivamente-errado");
        StorageException ex = assertThrows(StorageException.class, bad::open);
        assertEquals(530, ex.getReplyCode());   // 530 = "não logou" - resposta exposta do booleano _ 530 = "not logged in" — the boolean's reply, surfaced
    }
}
