# Rating Service

Manages food ratings and reviews. Triggered after delivery completion.

## Endpoints
- POST /api/v1/ratings — Submit rating (food + delivery + packaging)
- GET /api/v1/ratings/food/{foodId} — Get paginated ratings
- GET /api/v1/ratings/food/{foodId}/summary — Get average rating
