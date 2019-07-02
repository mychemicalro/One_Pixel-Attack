package com.voltage.demo.customer;

import com.voltage.securedata.enterprise.LibraryContext;
import com.voltage.securedata.enterprise.FPE;
import com.voltage.securedata.enterprise.VeException;
import com.voltage.vibesimple.AuthMethod;
import com.voltage.vibesimple.VibeSimple;
import com.voltage.demo.context.LibraryContextLoader;

public class Customer
{
  public String member_id  = "";
  public String name       = "";
  public String address    = "";
  public String telephone  = "";
  public String email      = "";
  public String birthdate  = "";

  private LibraryContext lc = null;
  private VibeSimple service = null;
  private FPE memberEncrypter = null;
  private FPE phoneEncrypter = null;
  private FPE stringEncrypter = null;
  private final String KEY_IDENTITY = "ciao@nttdata.com";
  private final String SHARED_SECRET = "Password1!";

  private final String DATE_PROTECTION_FORMAT = "data";

  public Customer() throws Exception
  {
    if (lc == null)
    {
      lc = LibraryContextLoader.getLibraryContext();
    }

    if (service == null)
    {
      service = LibraryContextLoader.getService();
    }

    System.out.println ("Loaded LibraryContext, now creating FPE objects...");
    memberEncrypter = lc.getFPEBuilder("fixed")
      .setSharedSecret(SHARED_SECRET) 
      .setIdentity(KEY_IDENTITY)
      .build();
    System.out.println ("Primary key FPE up.");

    stringEncrypter = lc.getFPEBuilder("An existing name, address, and email format name")
      .setSharedSecret(SHARED_SECRET)
      .setIdentity(KEY_IDENTITY)
      .build();
    System.out.println ("String FPE up.");

    phoneEncrypter = lc.getFPEBuilder("variable")
      .setSharedSecret(SHARED_SECRET)
      .setIdentity(KEY_IDENTITY)
      .build();
    System.out.println ("Telephone FPE up.");
  }

  private void exceptionHelper (boolean encrypt, String value, Exception e)
  {
    String operation;
    if (encrypt)
    {
      operation =  "Encryption of ";
    }
    else
    {
      operation = "Decryption of";
    }
    System.out.println (operation + value + " did not work.");
    System.out.println ("Message: " + e.getMessage());
  }

  public int encrypt ()
  {
    int result = 0;
    try
    {
      member_id = memberEncrypter.protect(member_id);
    }
    catch (Exception e)
    {
      exceptionHelper(true, member_id, e);
      result += 1;
    }

    try
    {
      name = stringEncrypter.protect(name);
    }
    catch (Exception f)
    {
      exceptionHelper(true, name, f);
      result += 2;
    }

    try
    {
      address = stringEncrypter.protect(address);
    }
    catch (Exception g)
    {
      exceptionHelper(true, address, g);
      result += 4;
    }

    try
    {
      telephone = phoneEncrypter.protect(telephone);
    }
    catch (Exception h)
    {
      exceptionHelper(true, telephone, h);
      result += 8;
    }

    try
    {
      birthdate = service.protectFormattedData(birthdate, DATE_PROTECTION_FORMAT, KEY_IDENTITY, null, AuthMethod.SHARED_SECRET, SHARED_SECRET);
    }
    catch (Exception i)
    {
      exceptionHelper(true, birthdate, i);
      result += 16;
    }

    try
    {
      email = stringEncrypter.protect(email);
    }
    catch (Exception j)
    {
      exceptionHelper(true, email, j);
      result += 32;
    }

    return result;
  }

  public int decrypt ()
  {
    int result = 0;

    try
    {
      member_id = memberEncrypter.access(member_id);
    }
    catch (Exception e)
    {
      exceptionHelper(false, member_id, e);
      result += 1;
    }

    try
    {
      name = stringEncrypter.access(name);
    }
    catch (Exception f)
    {
      exceptionHelper(false, name, f);
      result += 2;
    }

    try
    {
      address = phoneEncrypter.access(address);
    }
    catch (Exception g)
    {
      exceptionHelper(false, address, g);
      result += 4;
    }

    try
    {
      telephone = stringEncrypter.access(telephone);
    }
    catch (Exception h)
    {
      exceptionHelper(false, telephone, h);
      result += 8;
    }

    try
    {
      birthdate = service.accessFormattedData(birthdate, DATE_PROTECTION_FORMAT, KEY_IDENTITY, null, AuthMethod.SHARED_SECRET, SHARED_SECRET);
    }
    catch (Exception i)
    {
      exceptionHelper(false, birthdate, i);
      result += 16;
    }

    try
    {
      email = stringEncrypter.access(email);
    }
    catch (Exception j)
    {
      exceptionHelper(false, email, j);
      result += 32;
    }

    return result;
  }
}