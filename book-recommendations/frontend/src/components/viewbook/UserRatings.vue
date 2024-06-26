<template>
  <v-container v-if="!isLoading && $auth.isAuthenticated && $auth.user !== undefined">
    <div
      v-if="hasRating"
      class="ma-0"
    >
      <h3>
        Your rating
        <v-rating
          v-model="bookRating"
          background-color="orange lighten-3"
          color="orange"
          size="30"
          readonly
        />
        <v-dialog
          v-model="updateDialog"
          persistent
          width="300"
        >
          <template #activator="{ on, attrs }">
            <v-btn
              color="primary"
              dark
              x-small
              v-bind="attrs"
              v-on="on"
            >
              Update your rating
            </v-btn>
          </template>
          <v-card>
            <v-card-title class="text-h5">
              Add your rating
            </v-card-title>
            <v-rating
              v-model="bookRating"
              class="ml-5"
              background-color="orange lighten-3"
              color="orange"
              large
            />
            <v-card-actions>
              <v-spacer />
              <v-btn
                color="green darken-1"
                text
                @click="updateDialog = false"
              >
                close
              </v-btn>
              <v-btn
                color="green darken-1"
                text
                @click="updateUserRating"
              >
                Rate
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </h3>
    </div>
    <div v-else>
      <v-dialog
        v-model="dialog"
        persistent
        width="300"
      >
        <template #activator="{ on, attrs }">
          <v-btn
            color="primary"
            dark
            v-bind="attrs"
            v-on="on"
          >
            Add your rating
          </v-btn>
        </template>
        <v-card>
          <v-card-title class="text-h5">
            Add your rating
          </v-card-title>
          <v-rating
            v-model="bookRating"
            class="ml-5"
            background-color="orange lighten-3"
            color="orange"
            large
          />
          <v-card-actions>
            <v-spacer />
            <v-btn
              color="green darken-1"
              text
              @click="dialog = false"
            >
              close
            </v-btn>
            <v-btn
              color="green darken-1"
              text
              @click="postUserRating"
            >
              Rate
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </div>
  </v-container>
</template>

<script>
import {getUserRating, saveUserRating, updateUserRating} from "@/api/view-book";

export default {
  name: "UserRatings",
  props: {
    isbn: {type: String, required: true}
  },
  data: () => ({
    bookRating: '',
    hasRating: false,
    dialog: false,
    updateDialog: false,
    isLoading: true
  }),
  watch: {
    async isbn() {
      await this.fetchRatingsData();
    },
  },
  async mounted() {
    await this.fetchRatingsData();
  },
  methods: {
    async fetchRatingsData() {
      await this.getRatingsData();
      if (this.bookRating !== null && this.bookRating !== "") {
        this.hasRating = true;
      }
      this.isLoading = false;
      this.$emit('user-rating-loaded');
    },
    async getRatingsData() {
      const token = await this.$auth.getTokenSilently();
      this.bookRating = await getUserRating(this.$auth.user.email, this.isbn, token)
    },
    async postUserRating() {
      const token = await this.$auth.getTokenSilently();
      this.hasRating = true
      await saveUserRating(this.$auth.user.email, this.isbn, this.bookRating, token)
    },
    async updateUserRating() {
      const token = await this.$auth.getTokenSilently();
      this.hasRating = true
      this.updateDialog = false
      await updateUserRating(this.$auth.user.email, this.isbn, this.bookRating, token)
    }
  }
}
</script>

<style scoped>

</style>
