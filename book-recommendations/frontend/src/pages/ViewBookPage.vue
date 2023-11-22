<template>
  <v-container
    fill-height
  >
    <v-row
      v-if="isLoading"
      class="justify-center"
    >
      <circles-to-rhombuses-spinner />
    </v-row>
    <v-row
      v-if="bookData === null && !isLoading"
      class="pt-6"
    >
      <p>
        {{ errorMessage }}
      </p>
    </v-row>
    <v-row
      v-if="!isLoading && bookData !== null"
      class="pb-0 pt-0 align-center"
    >
      <v-col class="ml-4 pt-4">
        <!-- TODO BES-70 show all authors and genres depending on the data returned from the API -->
        <h1
          v-if="bookData.title"
          class="mt-0"
        >
          {{ bookData.title }}
        </h1>
        <div
          v-if="bookData.authors"
        >
          <div
            v-for="(author, index) in bookData.authors"
            :key="index"
          >
            <h2 v-if="index === 0 && bookData.authors.length === 1">
              By
              <a @click="emitAuthorSearch(author)">
                {{ author }}
              </a>
            </h2>
            <h2 v-else-if="index === 0">
              By
              <a @click="emitAuthorSearch(author)">
                {{ author }},
              </a>
            </h2>
            <h2 v-else-if="index === bookData.authors.length -1">
              <a @click="emitAuthorSearch(author)">
                {{ author }}
              </a>
            </h2>
            <h2 v-else>
              <a @click="emitAuthorSearch(author)">
                {{ author }},
              </a>
            </h2>
          </div>
        </div>
        <template v-if="!$auth.isAuthenticated">
          <v-alert
            outlined
            type="info"
            :width="$vuetify.breakpoint.xs ? '100%' : '75%'"
          >
            <a @click="$auth.loginWithRedirect()">
              Log in
            </a>
            to add your own ratings and manage collections
          </v-alert>
        </template>
        <template v-if="$auth.isAuthenticated && isIsbnValid">
          <template v-if="!ratingsLoaded && !collectionsLoaded">
            <v-progress-circular
              :size="30"
              :width="7"
              color="secondary"
              indeterminate
            />
          </template>
          <user-ratings
            :isbn="
              isbn.toString()"
            @user-rating-loaded="ratingsLoaded = true"
          />
          <book-collections
            :book-isbn="isbn.toString()"
            :book-data="bookData"
            @collections-loaded="collectionsLoaded = true"
          />
        </template>
      </v-col>
      <view-book-thumbnail :thumbnail="thumbnailUrl" />
    </v-row>
    <v-col
      v-if="isIsbnValid && !isLoading"
      :cols="getColsForBranding"
      :offset="getOffSetForBranding"
    >
      <v-img
        width="124"
        height="21"
        src="@/assets/poweredby_google.png"
        @click="goToGoogle"
      />
    </v-col>
    <v-row
      v-if="!isLoading && bookData !== null"
      class="pt-0 ma-0 align-center"
    >
      <about-book
        :category="concatDetails(bookData.categories)"
        :published-date="bookData.publishedDate"
        :original-description="bookData.description"
        :pages="bookData.pageCount"
        :publisher="bookData.publisher"
      />
    </v-row>
    <v-row
      v-if="recommendationsLoading"
      align="center"
      justify="center"
    >
      <book-spinner />
    </v-row>
    <v-row v-if="!isLoading && isIsbnValid && recommendations.length > 0">
      <book-category-carousel
        best-seller-category="You may also like"
        :books="recommendations"
        @populate-from-recommendations="populateFromRecommendation"
      />
    </v-row>
  </v-container>
</template>

<script>
import ViewBookThumbnail from "@/components/viewbook/ViewBookThumbnail";
import {getBookInfo} from "@/api/view-book";
import UserRatings from "@/components/viewbook/UserRatings";
import AboutBook from "@/components/viewbook/AboutBook";
import {EventBus} from "@/event-bus";
import BookCollections from "@/components/viewbook/BookCollections.vue";
import BookCategoryCarousel from "@/components/home/BookCategoryCarousel";
import {mapActions, mapGetters} from "vuex";
import {getRecommendationsStatus} from "@/api/features";
import BookSpinner from "@/assets/ui-components/BookSpinner.vue";
import CirclesToRhombusesSpinner from "@/assets/ui-components/CirclesToRhombusesSpinner.vue";

export default {
  name: 'ViewBook',
  components: {
    CirclesToRhombusesSpinner,
    BookSpinner,
    BookCollections,
    BookCategoryCarousel,
    AboutBook,
    UserRatings,
    ViewBookThumbnail
  },
  data: function () {
    return {
      bookData: null,
      isbn: "",
      isLoading: true,
      recommendationsLoading: false,
      previousBookData: null,
      viewBookEmitted: false,
      ratingsLoaded: false,
      collectionsLoaded: false,
      recommendations: []
    }
  },
  computed: {
    errorMessage() {
      return "No results found for ISBN: " + this.isbn
    },
    isIsbnValid() {
      return this.isbn ? (this.isbn.length === 10 || this.isbn.length === 13) : false
    },
    getColsForBranding() {
      const colsToTake = {
        xl: 2,
        lg: 2,
        md: 3,
        xs: 7
      };
      const cols = Object.keys(colsToTake).find(
        col => this.$vuetify.breakpoint[col]);

      return colsToTake[cols] || 5;
    },
    getOffSetForBranding() {
      const offSetValues = {
        xl: 9,
        lg: 9,
        md: 9,
        xs: 5
      };

      const offset = Object.keys(offSetValues).find(
        ofst => this.$vuetify.breakpoint[ofst]);

      return offSetValues[offset] || 9;
    },
    thumbnailUrl() {
      const defaultThumbnail = "https://play-lh.googleusercontent.com/_tslXR7zUXgzpiZI9t70ywHqWAxwMi8LLSfx8Ab4Mq4NUTHMjFNxVMwTM1G0Q-XNU80"
      if (this.bookData.imageLinks && this.bookData.imageLinks.thumbnail) {
        return this.bookData.imageLinks.thumbnail;
      }
      return this.bookData.thumbnail || defaultThumbnail;
    },
    ...mapGetters(['getUpdatedBookData', 'getRecommendations'])
  },
  async activated() {
    //view-book - from search results
    //view-book-other - from any other origin (home, collections - will perform a get request to get this data with isbn and title)
    //search-triggered - search by isbn
    EventBus.$on('view-book', this.populateBookData);
    EventBus.$on(['view-book-other', 'search-triggered'], this.getBookData);

    await this.$nextTick();

    if (this.viewBookEmitted === false) {
      this.bookData = this.previousBookData;
      this.isLoading = false;
      this.updateDocumentTitle();
      this.recommendations = this.getRecommendations;
    }
  },
  deactivated() {
    this._resetBookView();
    this.isLoading = true;
    EventBus.$off(['search-triggered', 'view-book', 'view-book-other']);
  },
  methods: {
    async populateBookData(bookData) {
      this.viewBookEmitted = true;
      if (bookData) {
        this.bookData = bookData;
        await this.updateBookData(bookData);
        this.updateDocumentTitle();
        this.isbn = bookData.isbn;
      }
      this.isLoading = false;
      if (bookData) {
        await this.fetchRecommendations();
      }
    },
    async populateFromRecommendation() {
      this.isLoading = true;
      this._resetBookView();
      this.bookData = this.getUpdatedBookData;
      await this.fetchRecommendations();
      this.isLoading = false;
    },
    async getBookData(queryData) {
      this.isLoading = true;
      this.viewBookEmitted = true;
      this.isbn = queryData.isbn || queryData.searchTerm; //when coming from SearchBar, isbn is added to searchTerm property
      if (!this.isIsbnValid) {
        this.bookData = null;
        this.isLoading = false;
        return;
      }
      try {
        this.bookData = await getBookInfo(this.isbn, queryData.title, queryData.authors);
        await this.updateBookData(this.bookData);
        this.updateDocumentTitle();
        await this.fetchRecommendations();
        this.isLoading = false;
      } catch (error) {
        this.isLoading = false;
      }
    },
    async emitAuthorSearch(author) {
      await this.$router.push({name: 'search'});
      EventBus.$emit('search-triggered', {
        searchType: 'author',
        searchTerm: author
      });
    },
    async fetchRecommendations() {
      if (await getRecommendationsStatus()) {
        this.recommendationsLoading = true;
        await this.updateRecommendations(this.getUpdatedBookData);
        this.recommendations = this.getRecommendations;
        this.recommendationsLoading = false;
      }
    },
    updateDocumentTitle() {
      document.title = this.bookData.title ?? `Book Details`;
      document.title += this.bookData?.authors ? ` by ${this.bookData.authors}` : '';
    },
    concatDetails(details) {
      if (!details) {
        return null;
      }
      if (details.length > 1) {
        return details.join(', ');
      }
      return details.toString();
    },
    goToGoogle() {
      window.open("https://www.google.com", "_blank")
    },
    _resetBookView() {
      this.previousBookData = this.bookData;
      this.bookData = null;
      this.viewBookEmitted = false;
      this.collectionsLoaded = false;
      this.ratingsLoaded = false;
      this.recommendations = []
    },
    ...mapActions([
      'updateBookData', 'updateRecommendations'
    ]),
  }
}
</script>

