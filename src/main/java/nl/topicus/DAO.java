package nl.topicus;

import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

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
}
