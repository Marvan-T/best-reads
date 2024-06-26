<template>
  <v-col>
    <a @click="emitViewBook">
      <v-hover
        v-slot="{ hover }"
        ref="hoverEffectRef"
        :disabled="$vuetify.breakpoint.xsOnly"
      >
        <v-sheet
          :color="getHoverEffect(hover)"
          class="d-flex flex-column align-center justify-center"
          :elevation="hover ? 12 : 0"
          rounded
        >
          <v-img
            class="rounded"
            max-width="200"
            max-height="300"
            :src="bookImageLink"
          />
          <div class="flex-wrap text-body-2 mt-1">
            {{ getUpdatedTitle(bookTitle) }}
          </div>
        </v-sheet>
      </v-hover>
    </a>
  </v-col>
</template>

<script>
import {titleCase} from "title-case";
import {EventBus} from "@/event-bus";
import {mapActions} from "vuex";

export default {
  name: "BookItem",
  props: {
    bookImageLink: {
      type: String,
      required: true,
      default: "Unavailable",
    },
    authors: {
      type: Array,
      required: true,
      default: () => [],
    },
    bookTitle: {
      type: String,
      required: true,
      default: "Unavailable",
    },
    isbn: {
      type: String,
      required: true
    },
    category: {
      type: String,
      required: true
    },
    bookData: {
      type: Object,
      required: true
    }
  },
  deactivated() {
    //clear the hover effect when navigating away from the page
    this.$refs.hoverEffectRef._data.isActive = false;
  },
  methods: {
    getUpdatedTitle(title) {
      return titleCase(title.toLowerCase());
    },
    getHoverEffect(hover) {
      return hover ? "blue-grey lighten-4" : "transparent";
    },
    async emitViewBook() {
      switch (this.$route.name) {
        case "book":
          await this.updateBookData(this.bookData)
          this.$emit("populate-from-recommendations")
          break;
        case "Root":
        case "homePage":
          if (this.$route.name !== "book") {
            await this.$router.push({name: this.category === "Series Books" ? 'search' : 'book'});
          }
          EventBus.$emit(this.category === "Series Books" ? 'search-triggered' : 'view-book-other',
            {
              searchType: 'title',
              searchTerm: this.bookTitle.toLowerCase(),
              isbn: this.isbn,
              title: this.bookTitle,
              authors: this.authors,
            });
          break;
      }
    },
    ...mapActions([
      'updateBookData'
    ]),
  }
}
</script>
