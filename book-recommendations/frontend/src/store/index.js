import Vue from 'vue'
import Vuex from 'vuex'
import { getRecommendations } from '@/api/recommendations';

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    collectionValidationRules: [
      v => !!v || 'Collection name is required',
      v => (v && v.length <= 15)
        || 'Collection name must be less than 15 characters'
    ],
    bookData: {},
    recommendations: []
  },
  getters: {
    collectionValidationRules: state => state.collectionValidationRules,
    getUpdatedBookData: state => {
      // to align with the 2 book models that we have now until collections are migrated
      const googleBooksId = state.bookData.googleBooksId || state.bookData.id;
      let updatedBookData = {
        ...state.bookData,
        googleBooksId
      };

      if (state.bookData.googleBooksId) { // recommendation
        let isbn13Object = state.bookData.industryIdentifiers.find(
          identifier => identifier.type === 'ISBN_13');
        updatedBookData.isbn = isbn13Object.identifier;
      } else { // from the API
        updatedBookData.industryIdentifiers = [{
          "Type": "ISBN_13",
          "Identifier": updatedBookData.isbn
        }];
        updatedBookData.thumbnail = updatedBookData.imageLinks.thumbnail;
      }

          // Check and update publishedDate
      if (state.bookData.publishedDate) {
        let parts = state.bookData.publishedDate.split('-');
        if (parts.length === 1) { // only year
          updatedBookData.publishedDate = `${parts[0]}-01-01`;
        } else if (parts.length === 2) { // year and month
          updatedBookData.publishedDate = `${parts[0]}-${parts[1]}-01`;
        }
      }

      // Authors?
      return updatedBookData;
    },
    getRecommendations: state => state.recommendations
  },
  mutations: {
    SET_BOOK_DATA: (state, bookData) => {
      state.bookData = bookData;
    },
    SET_RECOMMENDATIONS(state, recommendations) {
      state.recommendations = recommendations;
    }
  },
  actions: {
    updateBookData: ({commit}, bookData) => {
      commit('SET_BOOK_DATA', bookData);
    },
    async updateRecommendations({ commit }, bookData){
      const result = await getRecommendations(bookData);
      if (result.success) {
        const recommendations = result.data;
        commit('SET_RECOMMENDATIONS', recommendations);
      }
    }
  },
  modules: {}
});
