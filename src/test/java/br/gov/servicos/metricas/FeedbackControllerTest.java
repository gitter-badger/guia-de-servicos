package br.gov.servicos.metricas;

import br.gov.servicos.foundation.exceptions.ValidacaoFormularioException;
import lombok.experimental.FieldDefaults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.view.RedirectView;

import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@FieldDefaults(level = PRIVATE)
public class FeedbackControllerTest {

    public static final String TICKET = "a995f68470b9";

    @Mock
    FeedbackRepository feedbacks;

    FeedbackController controller;

    @Before
    public void setup() {
        controller = new FeedbackController(feedbacks);
    }

    @Test
    public void retornaFeedbackAgradecimentoParaOUsuario() {
        RedirectView response = controller.feedback("/", null, "a", "b", TICKET);
        assertThat(response.getUrl(), is("/feedback/obrigado"));
    }

    @Test
    public void aceitaFeedbackDoUsuarioSeTiverApenasOQueTentouFazer() throws Exception {
        RedirectView response = controller.feedback("/", null, "tentou fazer", null, TICKET);
        assertThat(response.getUrl(), is("/feedback/obrigado"));
    }

    @Test
    public void aceitaFeedbackDoUsuarioSeTiverApenasOQueAcontece() throws Exception {
        RedirectView response = controller.feedback("/", null, null, "aconteceu", TICKET);
        assertThat(response.getUrl(), is("/feedback/obrigado"));
    }

    @Test(expected = ValidacaoFormularioException.class)
    public void retorna400AoPreencherFormularioComCamposVazios() {
        controller.feedback(null, null, null, null, null);
    }

    @Test
    public void deveSalvarOFeedbackDoUsuario() {
        controller.feedback("localhost", null, "Estou tentando mandar feedback", "E está tudo certo :)", TICKET);

        verify(feedbacks).save(new Feedback()
                .withUrl("localhost")
                .withQueryString(null)
                .withTimestamp(anyLong())
                .withTentandoFazer("Estou tentando mandar feedback")
                .withAconteceu("E está tudo certo :)"));
    }

    @Test
    public void exibeMensagemDeAgradecimento() throws Exception {
        assertThat(controller.obrigado().getViewName(), is("obrigado"));
    }
}
