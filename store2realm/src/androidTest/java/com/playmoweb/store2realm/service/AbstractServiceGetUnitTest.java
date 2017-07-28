package com.playmoweb.store2realm.service;

import android.support.test.runner.AndroidJUnit4;

import com.playmoweb.store2realm.mock.MemoryService;
import com.playmoweb.store2realm.mock.TestModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Test all get operations
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    28/02/2017
 */
@RunWith(AndroidJUnit4.class)
public class AbstractServiceGetUnitTest {

    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private MemoryService service = new MemoryService(TestModel.class);

    @Before
    public void before() {

    }

    @After
    public void after() {
        subscriptions.clear();
    }

    @Test
    public void getOne() throws Exception {

    }
}
