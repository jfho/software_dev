package dtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.*;
import dtu.Models.BankAccount;
import dtu.Models.Customer;
import dtu.Models.Merchant;
import dtu.Models.Transaction;
import dtu.ws.fastmoney.Account;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

public class SimpleDTUPaySteps {}