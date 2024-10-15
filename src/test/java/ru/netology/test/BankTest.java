package ru.netology.test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;


import static com.codeborne.selenide.Selenide.open;

public class BankTest {

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }


    @Test
    public void shouldTransferMoneyFromCardFirstToSecondCard() {
        DashboardPage dashboardPage = new DashboardPage();

        var cardOneInfo = DataHelper.getFirstCard();
        var cardTwoInfo = DataHelper.getSecondCard();
        var cardOneBalance = dashboardPage.getCardBalance(cardOneInfo);
        var cardTwoBalance = dashboardPage.getCardBalance(cardTwoInfo);
        var sum = DataHelper.getValidSum(cardOneBalance);

        var transfer = dashboardPage.selectCard(cardTwoInfo);
        transfer.getValidTransfer(String.valueOf(sum), cardOneInfo);

        Assertions.assertEquals(cardOneBalance - sum, dashboardPage.getCardBalance(cardOneInfo));
        Assertions.assertEquals(cardTwoBalance + sum, dashboardPage.getCardBalance(cardTwoInfo));
    }

    @Test
    public void shouldTransferMoneyFromCardSecondToFirstCard() {
        DashboardPage dashboardPage = new DashboardPage();

        var cardOneInfo = DataHelper.getFirstCard();
        var cardTwoInfo = DataHelper.getSecondCard();
        var cardOneBalance = dashboardPage.getCardBalance(cardOneInfo);
        var cardTwoBalance = dashboardPage.getCardBalance(cardTwoInfo);
        var sum = DataHelper.getValidSum(cardTwoBalance);

        var transfer = dashboardPage.selectCard(cardOneInfo);
        transfer.getValidTransfer(String.valueOf(sum), cardTwoInfo);

        Assertions.assertEquals(cardTwoBalance - sum, dashboardPage.getCardBalance(cardTwoInfo));
        Assertions.assertEquals(cardOneBalance + sum, dashboardPage.getCardBalance(cardOneInfo));
    }

    @Test
    public void shouldTransferMoneyCancel() {
        DashboardPage dashboardPage = new DashboardPage();

        var cardOneInfo = DataHelper.getFirstCard();
        var cardTwoInfo = DataHelper.getSecondCard();
        var cardOneBalance = dashboardPage.getCardBalance(cardOneInfo);
        var cardTwoBalance = dashboardPage.getCardBalance(cardTwoInfo);

        var transfer = dashboardPage.selectCard(cardOneInfo);
        transfer.cancelRefill();

        Assertions.assertEquals(cardTwoBalance, dashboardPage.getCardBalance(cardTwoInfo));
        Assertions.assertEquals(cardOneBalance, dashboardPage.getCardBalance(cardOneInfo));
    }

    @Test
    public void shouldTransferMoneyMoreThanBalance() {
        DashboardPage dashboardPage = new DashboardPage();

        var cardOneInfo = DataHelper.getFirstCard();
        var cardTwoInfo = DataHelper.getSecondCard();
        var cardTwoBalance = dashboardPage.getCardBalance(cardTwoInfo);
        var sum = DataHelper.getInvalidSum(cardTwoBalance);

        var transfer = dashboardPage.selectCard(cardOneInfo);
        transfer.refillCard(String.valueOf(sum), cardTwoInfo);
        transfer.getInvalidTransfer("Ошибка! Недостаточно средств на карте!");
    }
}