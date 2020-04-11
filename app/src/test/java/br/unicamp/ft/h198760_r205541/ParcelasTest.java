package br.unicamp.ft.h198760_r205541;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ParcelasTest {

    private Financiamento financiamento;

    @Before
    public void setUp(){
        financiamento = new Financiamento();

    }

    @Test
    public void testParcelas_isNotNull(){
        financiamento.setParcela("0");
        assertEquals("0",financiamento.getParcela());
    }

}
