package nl.topicus;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.Cache;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	@Inject
	private DAO dao;

//	@Resource(lookup = "java:jboss/infinispan/container/hibernate")
//	@Resource(lookup = "java:jboss/infinispan/cache/hibernate/entity")
//	private Cache cache;

	private long lastId = 0L;

	public HomePage() {
		Form<Void> form = new Form<>("form");
		add(form);

		NumberTextField<Integer> aantal = new NumberTextField<>("aantal", new Model<>(10), Integer.class);
		aantal.setStep(1);
		form.add(aantal);

		form.add(new Button("insert") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				dao.insert(aantal.getModelObject());
			}
		});

		form.add(new Button("fillCache") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				dao.fillCache();
			}
		});

		form.add(new Button("wipeCache") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				dao.wipeCache();
			}
		});

		form.add(new Button("wipeDB") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				dao.wipeDB();
			}
		});

		NumberTextField<Integer> value = new NumberTextField<>("value", new Model<>(), Integer.class);
		form.add(value);

		CheckBox useCache = new CheckBox("useCache", new Model<>(true));
		form.add(useCache);
		LoadableDetachableModel<List<NoJpaEntity>> entities = new LoadableDetachableModel<List<NoJpaEntity>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<NoJpaEntity> load() {
				return dao.list(useCache.getModelObject());
			}
		};

		ListView<NoJpaEntity> list = new ListView<NoJpaEntity>("list", entities) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<NoJpaEntity> item) {
				item.add(new Label("id", item.getModelObject().getId()));
				item.add(new Label("version", item.getModelObject().getVersion()));
				item.add(new Label("value", item.getModelObject().getValue()));
				item.add(new Button("inc") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSubmit() {
						MyEntity e = dao.read(item.getModelObject().getId());
						e.setValue(e.getValue() + 1);
						entities.detach();
					}
				});
				item.add(new Button("update") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSubmit() {
						dao.updateViaCriteria(item.getModelObject().getId());
						entities.detach();
					}
				});
				item.add(new Button("read") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSubmit() {
						value.setModelObject(dao.read(item.getModelObject().getId()).getValue());
					}
				});
				item.add(new Button("modifyRollback") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSubmit() {
						MyEntity e = dao.read(item.getModelObject().getId());
						e.setValue(e.getValue() + 1);
						((HttpServletRequest) getWebRequest().getContainerRequest()).setAttribute("rollback", true);
						entities.detach();
					}
				});
				item.add(new Button("delete") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSubmit() {
						dao.delete(item.getModelObject().getId());
					}
				});
			}
		};
		form.add(list);

		form.add(new Button("new") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				MyEntity myEntity = new MyEntity();
				myEntity.setValue(1);
				dao.persist(myEntity);
				lastId = myEntity.getId();
				dao.flush();
			}
		});
		
		form.add(new Button("update") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				dao.setCacheModeGet();
				MyEntity lastEntity = dao.read(lastId);
				lastEntity.setValue(lastEntity.getValue() + 1);
				dao.flush();
			}
		});

		form.add(new Button("listEntititiesInCache") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit()
			{
				try
				{
					logCurrentCache();
				}
				catch (NamingException e)
				{
					throw new RuntimeException(e);
				}
			}
		 });


	}
	public void logCurrentCache() throws NamingException
	{
		System.out.println("hello");
		Context context = new InitialContext();
		EmbeddedCacheManager cacheManager = (EmbeddedCacheManager) context.lookup("java:jboss/infinispan/container/hibernate");


//		Statistics statistics = cache.getSessionFactory().getStatistics();
//		for (String entityName : statistics.getEntityNames())
//		{
//			System.out.println("entity: " + entityName);
//			EntityStatistics entityStatistics = statistics.getEntityStatistics(entityName);
//			System.out.println(entityStatistics);
//		}

	}


}
