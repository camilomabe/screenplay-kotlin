import net.serenitybdd.core.pages.WebElementFacade
import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.GivenWhenThen.seeThat
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import net.serenitybdd.screenplay.actions.Click
import net.serenitybdd.screenplay.actions.Enter
import net.serenitybdd.screenplay.actions.Open
import net.serenitybdd.screenplay.actions.SelectFromOptions
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers
import net.serenitybdd.screenplay.questions.Text
import net.serenitybdd.screenplay.questions.WebElementQuestion
import net.serenitybdd.screenplay.targets.Target
import net.serenitybdd.screenplay.waits.WaitUntil
import net.thucydides.core.annotations.Managed
import org.junit.Test
import org.junit.runner.RunWith
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

@RunWith(SerenityRunner::class)
class WebTest {

    @Managed
    var webDriver: WebDriver? = null
    val camilo = Actor.named("Camilo")
    val subjectHeading = Target.the("Subject heading").located(By.cssSelector("select#id_contact"))
    val emailAddress = Target.the("Email Address").located(By.cssSelector("input#email"))
    val orderReference = Target.the("Order reference").located(By.cssSelector("input#id_order"))
    val message = Target.the("Tesx area").located(By.cssSelector("textarea#message"))
    val send = Target.the("Send message").located(By.cssSelector("button#submitMessage"))
    val errorMessage = Target.the("Error message").located(By.cssSelector("div.alert-danger"))
    val products = Target.the("Product").locatedBy("ul#homefeatured li div.product-container")
    val productInfo = Target.the("info product ").located(By.cssSelector("div.box-info-product"))
    val productPrice = "div.left-block span.product-price"
    val productName = "div.right-block a.product-name"
    val sendMessageUrl = "http://automationpractice.com/index.php?controller=contact"
    val productUrl = "http://automationpractice.com/index.php"

    @Test
    fun navigateToContactPageAndSenMessage(){


        camilo.can(BrowseTheWeb.with(webDriver))
        camilo.attemptsTo(
            Open.url(sendMessageUrl),
            SelectFromOptions.byVisibleText("Customer service").from(subjectHeading),
            SelectFromOptions.byValue("1").from(subjectHeading),
            Enter.theValue("email@hotmail.com").into(emailAddress),
            Enter.theValue("123456").into(orderReference),
            Enter.theValue("Send message ").into(message),
            Click.on(send)
        )
    }

    @Test
    fun invalidEmailAddress(){

        camilo.can(BrowseTheWeb.with(webDriver))
        camilo.attemptsTo(
            Open.url(sendMessageUrl),
            SelectFromOptions.byVisibleText("Customer service").from(subjectHeading),
            Click.on(send)
        )

        val error1 = errorMessage.resolveFor(camilo).text
        val error2 = Text.of(errorMessage).asAString().answeredBy(camilo)

        camilo.should(
            seeThat(WebElementQuestion.the(errorMessage),
                WebElementStateMatchers.isVisible())
        )

        println("Message' value 1 is:  $error1")
        println("Message' value 2 is:   $error2")
    }

    @Test
    fun waitUntilElementIsPresent(){
        camilo.can(BrowseTheWeb.with(webDriver))
        camilo.attemptsTo(
            Open.url(sendMessageUrl),
        )
        camilo.attemptsTo(Click.on(send))

        val error1 = errorMessage.resolveFor(camilo).text
        val error2 = Text.of(errorMessage).asAString().answeredBy(camilo)

        camilo.attemptsTo(
            WaitUntil.the(errorMessage, WebElementStateMatchers.isVisible()).forNoMoreThan(12).seconds()
        )
        println("Message' value 1 is:  $error1")
        println("Message' value 2 is:   $error2")
    }

    @Test
    fun selectRandomProductFromList(){
        camilo.can(BrowseTheWeb.with(webDriver))
        camilo.attemptsTo(
            Open.url(productUrl)
        )
        val randomProduct = products.resolveAllFor(camilo).random()
        println("The random product is : ${randomProduct.text}")
        println("Product information   $productInfo")
        randomProduct.click()
        camilo.should(seeThat(WebElementQuestion.the(productInfo),WebElementStateMatchers.isVisible()))
    }


    @Test
    fun gettingPriceAndNameFromProducts(){
        camilo.can(BrowseTheWeb.with(webDriver))
        camilo.attemptsTo(
            Open.url(productUrl)
        )
        val productData = mutableListOf<WebProductInfo>()
        val allProductElements = products
            .resolveAllFor(camilo).forEach{
                productData.add(
                    WebProductInfo(
                        it.findBy<WebElementFacade>(By.cssSelector(productPrice)).textContent,
                        it.findBy<WebElementFacade>(By.cssSelector(productName)).textContent
                    )
                )
            }
        productData.forEach {
            println(it.toString())
        }
        println("$allProductElements")
    }
}