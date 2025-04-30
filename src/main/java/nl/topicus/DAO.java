package nl.topicus;

import java.util.List;
import java.util.Random;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import org.hibernate.CacheMode;
import org.hibernate.Session;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DAO {
	@PersistenceContext
	private EntityManager em;

	@Resource
	private EJBContext context;

	public void markForRollback() {
		context.setRollbackOnly();
	}

	public void insert(int aantal) {
		for (int i = 0; i < aantal; i++)
			em.persist(new MyEntity());
	}

	public void fillCache() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MyEntity> criteria = cb.createQuery(MyEntity.class);
		Root<MyEntity> root = criteria.from(MyEntity.class);
		criteria.select(root);
		TypedQuery<MyEntity> query = em.createQuery(criteria).setHint("org.hibernate.cacheable", true);
		query.getResultList();
	}

	public void wipeCache() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<MyEntity> delete = cb.createCriteriaDelete(MyEntity.class);
		Root<MyEntity> root = delete.from(MyEntity.class);
		delete.where(cb.equal(root.get("id"), -1));
		em.createQuery(delete).executeUpdate();
	}

	public void delete(Long id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<MyEntity> delete = cb.createCriteriaDelete(MyEntity.class);
		Root<MyEntity> root = delete.from(MyEntity.class);
		delete.where(cb.equal(root.get("id"), id));
		em.createQuery(delete).executeUpdate();
	}

	public void wipeDB() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<MyEntity> delete = cb.createCriteriaDelete(MyEntity.class);
		delete.from(MyEntity.class);
		em.createQuery(delete).executeUpdate();
	}

	public List<NoJpaEntity> list(boolean useCache) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<NoJpaEntity> criteria = cb.createQuery(NoJpaEntity.class);
		Root<MyEntity> root = criteria.from(MyEntity.class);
		criteria.orderBy(cb.asc(root.get("id")));
		criteria.select(cb.construct(NoJpaEntity.class, root.get("id"), root.get("version"), root.get("value")));
		TypedQuery<NoJpaEntity> query = em.createQuery(criteria).setHint("org.hibernate.cacheable", useCache);
		return query.getResultList();
	}

	public MyEntity read(long id) {
		return em.find(MyEntity.class, id);
	}

	public void updateViaCriteria(long id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<MyEntity> update = cb.createCriteriaUpdate(MyEntity.class);
		Root<MyEntity> root = update.from(MyEntity.class);
		update.where(cb.equal(root.get("id"), id));
		update.set("value", new Random().nextInt());
		em.createQuery(update).executeUpdate();
	}

	public void updateViaEntity(MyEntity entity) {
		entity.setValue(new Random().nextInt());
		em.flush();
	}
	
	public void persist(MyEntity myEntity)
	{
		em.persist(myEntity);
	}
	
	public void flush()
	{
		em.flush();
	}
	
	public void setCacheModeGet()
	{
		em.unwrap(Session.class).setCacheMode(CacheMode.GET);
	}
}
