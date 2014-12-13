/**

    Copyright 2007 Engineering Ingegneria Informatica S.p.A.

    This file is part of Spagic.

    Spagic is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    any later version.

    Spagic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
**/
package it.eng.eli4u.imieidati.services.database.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HibernateSessionFactory {
	private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactory.class);
	
    private static final SessionFactory _sessionFactory;
    
    static {
        try {
            _sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch(Throwable ex) {
        	logger.error(ex.getLocalizedMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return _sessionFactory;
    }
}
