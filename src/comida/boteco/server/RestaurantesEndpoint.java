package comida.boteco.server;

import comida.boteco.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "restaurantesendpoint", namespace = @ApiNamespace(ownerDomain = "boteco.comida", ownerName = "boteco.comida", packagePath = "server"))
public class RestaurantesEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listRestaurantes")
	public CollectionResponse<Restaurantes> listRestaurantes(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Restaurantes> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr
					.createQuery("select from Restaurantes as Restaurantes");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Restaurantes>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Restaurantes obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Restaurantes> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getRestaurantes")
	public Restaurantes getRestaurantes(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		Restaurantes restaurantes = null;
		try {
			restaurantes = mgr.find(Restaurantes.class, id);
		} finally {
			mgr.close();
		}
		return restaurantes;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param restaurantes the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertRestaurantes")
	public Restaurantes insertRestaurantes(Restaurantes restaurantes) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsRestaurantes(restaurantes)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(restaurantes);
		} finally {
			mgr.close();
		}
		return restaurantes;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param restaurantes the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateRestaurantes")
	public Restaurantes updateRestaurantes(Restaurantes restaurantes) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsRestaurantes(restaurantes)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(restaurantes);
		} finally {
			mgr.close();
		}
		return restaurantes;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeRestaurantes")
	public void removeRestaurantes(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			Restaurantes restaurantes = mgr.find(Restaurantes.class, id);
			mgr.remove(restaurantes);
		} finally {
			mgr.close();
		}
	}

	private boolean containsRestaurantes(Restaurantes restaurantes) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Restaurantes item = mgr.find(Restaurantes.class,
					restaurantes.getId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
