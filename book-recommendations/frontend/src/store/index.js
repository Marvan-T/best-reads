import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    collectionValidationRules: [
      v => !!v || 'Collection name is required',
      v => (v && v.length <= 15)
        || 'Collection name must be less than 15 characters'
    ],
    bookData: {}
  },
  getters: {
    collectionValidationRules: state => state.collectionValidationRules,
    getUpdatedBookData: state => {
      // to align with the 2 book models that we have now until collections are migrated
      const googleBooksId = state.bookData.googleBooksId || state.bookData.id;
      let updatedBookData = {
        ...state.bookData,
        GoogleBooksId: googleBooksId
      };

      if (state.bookData.googleBooksId) {
        let isbn13Object = state.bookData.industryIdentifiers.find(
          identifier => identifier.type === 'ISBN_13');
        updatedBookData.isbn = isbn13Object.identifier;
      } else {
        updatedBookData.industryIdentifiers = [{
          "Type": "ISBN_13",
          "Identifier": updatedBookData.isbn
        }]
      }
      // Authors?
      return updatedBookData;
    }
  },
  mutations: {
    SET_BOOK_DATA: (state, bookData) => {
      state.bookData = bookData;
    }
  },
  actions: {
    updateBookData: ({commit}, bookData) => {
      commit('SET_BOOK_DATA', bookData);
    }
  },
  modules: {}
});
