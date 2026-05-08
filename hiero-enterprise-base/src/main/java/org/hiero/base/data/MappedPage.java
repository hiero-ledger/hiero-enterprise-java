package org.hiero.base.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link Page} adapter that maps items from another page while preserving pagination behavior.
 *
 * @param <S> source element type
 * @param <T> target element type
 */
public final class MappedPage<S, T> implements Page<T> {

  private final Page<S> delegate;

  private final Function<S, Optional<T>> mapper;

  public MappedPage(final Page<S> delegate, final Function<S, Optional<T>> mapper) {
    this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
  }

  @Override
  public int getPageIndex() {
    return delegate.getPageIndex();
  }

  @Override
  public int getSize() {
    return getData().size();
  }

  @Override
  public List<T> getData() {
    return delegate.getData().stream().map(mapper).flatMap(Optional::stream).toList();
  }

  @Override
  public boolean hasNext() {
    return delegate.hasNext();
  }

  @Override
  public Page<T> next() {
    return new MappedPage<>(delegate.next(), mapper);
  }

  @Override
  public Page<T> first() {
    return new MappedPage<>(delegate.first(), mapper);
  }

  @Override
  public boolean isFirst() {
    return delegate.isFirst();
  }
}
