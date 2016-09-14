package com.cooloongwu.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.cooloongwu.coolchat.entity.Conversation;

import com.cooloongwu.greendao.gen.ConversationDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig conversationDaoConfig;

    private final ConversationDao conversationDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        conversationDaoConfig = daoConfigMap.get(ConversationDao.class).clone();
        conversationDaoConfig.initIdentityScope(type);

        conversationDao = new ConversationDao(conversationDaoConfig, this);

        registerDao(Conversation.class, conversationDao);
    }

    public void clear() {
        conversationDaoConfig.clearIdentityScope();
    }

    public ConversationDao getConversationDao() {
        return conversationDao;
    }

}