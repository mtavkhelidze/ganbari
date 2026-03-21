package base

trait Iso[Raw, Mine] {
  def from(a: Raw): Mine
  def to(a: Mine): Raw
}
