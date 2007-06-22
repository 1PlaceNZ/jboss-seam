package org.jboss.seam.util;

import javax.transaction.UserTransaction;

import org.jboss.seam.core.BasicTransactionListener;
import org.jboss.seam.core.TransactionListener;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;

/**
 * Performs work in a JTA transaction.
 * 
 * @author Gavin King
 */
public abstract class Work<T>
{
   private static final LogProvider log = Logging.getLogProvider(Work.class);
   
   protected abstract T work() throws Exception;
   
   protected boolean isNewTransactionRequired(boolean transactionActive)
   {
      return !transactionActive;
   }
   
   public final T workInTransaction() throws Exception
   {
      boolean transactionActive = Transaction.instance().isActiveOrMarkedRollback();
      boolean begin = isNewTransactionRequired(transactionActive);
      UserTransaction userTransaction = begin ? Transaction.instance() : null;
      TransactionListener transactionListener = BasicTransactionListener.instance();
      boolean success=false;
      
      if (begin) 
      {
         log.debug("beginning transaction");
         userTransaction.begin();
      }
      
      try
      {
         T result = work();
         if (begin) 
         {
            log.debug("committing transaction");
            transactionListener.beforeSeamManagedTransactionCompletion();
            userTransaction.commit();
            success = true;
         }
         return result;
      }
      catch (Exception e)
      {
         if (begin) 
         {
            log.debug("rolling back transaction");
            userTransaction.rollback();
         }
         throw e;
      }
      finally
      {
         if (begin)
         {
            transactionListener.afterSeamManagedTransactionCompletion(success);
         }
      }
      
   }
}
